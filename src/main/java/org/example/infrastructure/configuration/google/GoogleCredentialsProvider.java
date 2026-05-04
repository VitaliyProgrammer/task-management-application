package org.example.infrastructure.configuration.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.gson.GsonFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.RequiredArgsConstructor;
import org.example.infrastructure.configuration.properties.GoogleCalendarProperties;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleCredentialsProvider {

    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleCalendarProperties properties;
    private final ResourceLoader resourceLoader;

    public GoogleClientSecrets loadClientSecrets() throws IOException {
        try (InputStream inputStream =
                     resourceLoader.getResource(properties.getCredentialsPath()).getInputStream()) {

            return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(inputStream));
        }
    }
}
