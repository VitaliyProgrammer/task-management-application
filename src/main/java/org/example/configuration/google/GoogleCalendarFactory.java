package org.example.configuration.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.UserCredentials;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.configuration.properties.GoogleCalendarProperties;
import org.example.entity.User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleCalendarFactory {

    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String APPLICATION_NAME = "Task Management Application";
    private final GoogleCalendarProperties properties;

    private final GoogleCredentialsProvider credentialsProvider;

    public Calendar createForUser(User user) {

        if (user.getGoogleRefreshToken() == null) {
            throw new IllegalStateException(
                    "Google Calendar is not connected for user id=" + user.getId()
            );
        }

        try {
            HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();

            GoogleCredentials googleCredentials = buildUserCredentials(user);

            Calendar calendar = new Calendar.Builder(
                    transport,
                    JSON_FACTORY,
                    new HttpCredentialsAdapter(googleCredentials)
            )
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            return calendar;
        } catch (Exception exception) {
            throw new IllegalArgumentException("Failed to create Google Calendar for client="
                    + user.getId(), exception);
        }
    }

    private GoogleCredentials buildUserCredentials(User user) throws IOException {

        GoogleClientSecrets clientSecrets = credentialsProvider.loadClientSecrets();

        UserCredentials userCredentials = UserCredentials.newBuilder()
                .setClientId(clientSecrets.getDetails().getClientId())
                .setClientSecret(clientSecrets.getDetails().getClientSecret())
                .setRefreshToken(user.getGoogleRefreshToken())
                .build();

        return userCredentials;
    }
}
