package org.example.infrastructure.configuration.dropbox;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.http.StandardHttpRequestor;
import com.dropbox.core.v2.DbxClientV2;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(DropBoxProperties.class)
public class DropBoxConfiguration {

    private static final String CERTIFICATE_PATH = "dropbox/dropboxSSL.crt";

    private final DropBoxProperties dropBoxProperties;

    @Bean
    public DbxClientV2 dbxClientV2() throws Exception {
        SSLContext sslContext = buildSslContext();

        StandardHttpRequestor requestor = new StandardHttpRequestor(
                StandardHttpRequestor.Config.DEFAULT_INSTANCE) {
            @Override
            protected void configureConnection(HttpsURLConnection connection) {
                connection.setSSLSocketFactory(sslContext.getSocketFactory());
            }
        };

        DbxRequestConfig configuration = DbxRequestConfig
                .newBuilder("task-management-application")
                .withHttpRequestor(requestor)
                .build();

        return new DbxClientV2(configuration, dropBoxProperties.accessToken());
    }

    private SSLContext buildSslContext() throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        X509Certificate dropboxCert;
        try (InputStream certStream = new ClassPathResource(CERTIFICATE_PATH).getInputStream()) {
            dropboxCert = (X509Certificate) cf.generateCertificate(certStream);
        }

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream defaultTrustStore = defaultTrustStoreStream()) {
            trustStore.load(defaultTrustStore, "changeit".toCharArray());
        }
        trustStore.setCertificateEntry("dropbox", dropboxCert);

        TrustManagerFactory tmf =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;
    }

    private InputStream defaultTrustStoreStream() throws Exception {
        String javaHome = System.getProperty("java.home");
        java.io.File certFile = new java.io.File(
                javaHome + "/lib/security/cacerts");
        return new java.io.FileInputStream(certFile);
    }
}
