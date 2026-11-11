package org.example.infrastructure.configuration.dropbox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.StandardHttpRequestor;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Dropbox client. An extra trusted certificate is optional: when
 * {@code dropbox/dropboxSSL.crt} is on the classpath it is added to the JDK trust store,
 * otherwise the default trust store is used (CI, Docker, cloud hosting).
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(DropBoxProperties.class)
public class DropBoxConfiguration {

    private static final String CLIENT_IDENTIFIER = "task-management-application";

    private static final String CERTIFICATE_PATH = "dropbox/dropboxSSL.crt";

    private static final long ALREADY_EXPIRED = 0L;

    private final DropBoxProperties dropBoxProperties;

    @Bean
    public DbxClientV2 dbxClientV2() throws Exception {

        DbxRequestConfig.Builder builder = DbxRequestConfig.newBuilder(CLIENT_IDENTIFIER);

        ClassPathResource certificate = new ClassPathResource(CERTIFICATE_PATH);

        if (certificate.exists()) {
            builder.withHttpRequestor(requestorTrusting(certificate));
        } else {
            log.info("No custom Dropbox certificate found, using the default JDK trust store");
        }

        return new DbxClientV2(builder.build(), credential());
    }

    /**
     * With a refresh token the access token starts as "already expired" (expiresAt = 0), so the
     * SDK fetches a fresh one before the first call and renews it automatically afterwards.
     */
    private DbxCredential credential() {

        if (dropBoxProperties.usesRefreshToken()) {
            log.info("Dropbox: using refresh token, access tokens are renewed automatically");

            return new DbxCredential(
                    "",
                    ALREADY_EXPIRED,
                    dropBoxProperties.refreshToken(),
                    dropBoxProperties.appKey(),
                    dropBoxProperties.appSecret());
        }

        String accessToken = dropBoxProperties.accessToken();

        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalStateException(
                    "Dropbox is not configured: set dropbox.refresh-token, dropbox.app-key and "
                            + "dropbox.app-secret (or dropbox.access-token for local development)");
        }

        log.warn("Dropbox: using a short-lived access token, it stops working after ~4 hours");

        return new DbxCredential(accessToken);
    }

    private StandardHttpRequestor requestorTrusting(ClassPathResource certificate)
            throws Exception {

        SSLContext sslContext = buildSslContext(certificate);

        return new StandardHttpRequestor(StandardHttpRequestor.Config.DEFAULT_INSTANCE) {
            @Override
            protected void configureConnection(HttpsURLConnection connection) {
                connection.setSSLSocketFactory(sslContext.getSocketFactory());
            }
        };
    }

    private SSLContext buildSslContext(ClassPathResource certificate) throws Exception {

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

        X509Certificate dropboxCertificate;
        try (InputStream certificateStream = certificate.getInputStream()) {
            dropboxCertificate =
                    (X509Certificate) certificateFactory.generateCertificate(certificateStream);
        }

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream defaultTrustStore = defaultTrustStoreStream()) {
            trustStore.load(defaultTrustStore, "changeit".toCharArray());
        }
        trustStore.setCertificateEntry("dropbox", dropboxCertificate);

        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        return sslContext;
    }

    private InputStream defaultTrustStoreStream() throws Exception {

        File trustStoreFile = new File(System.getProperty("java.home") + "/lib/security/cacerts");

        return new FileInputStream(trustStoreFile);
    }
}
