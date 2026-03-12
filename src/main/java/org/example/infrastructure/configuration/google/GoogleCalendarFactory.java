package org.example.infrastructure.configuration.google;

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
import org.example.domain.entity.User;
import org.example.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleCalendarFactory {

    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Task Management Application";

    private final GoogleCredentialsProvider credentialsProvider;

    private final UserRepository userRepository;

    public Calendar createForUser(User user) {

        User savedUser = validateAndLoadUser(user);

        try {
            HttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();

            GoogleCredentials googleCredentials = buildUserCredentials(savedUser);

            return new Calendar.Builder(
                    transport, JSON_FACTORY, new HttpCredentialsAdapter(googleCredentials))
                    .setApplicationName(APPLICATION_NAME)
                    .build();


        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Failed to create Google Calendar for client=" + user.getId(), exception);
        }
    }

    private GoogleCredentials buildUserCredentials(User user) throws IOException {

        GoogleClientSecrets clientSecrets = credentialsProvider.loadClientSecrets();

        return UserCredentials.newBuilder()
                .setClientId(clientSecrets.getDetails().getClientId())
                .setClientSecret(clientSecrets.getDetails().getClientSecret())
                .setRefreshToken(user.getGoogleRefreshToken())
                .build();
    }

    private User validateAndLoadUser(User user) {

        Long userId = user.getId();

        User persistedUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found for id=" + userId));

        String refreshToken = persistedUser.getGoogleRefreshToken();

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalStateException(
                    "Google Calendar is not connected for user id=" + user.getId());
        }

        return persistedUser;
    }
}
