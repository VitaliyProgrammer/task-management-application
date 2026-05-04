package org.example.infrastructure.integration;

import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.CalendarScopes;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.domain.entity.GoogleOAuthState;
import org.example.domain.entity.User;
import org.example.domain.exception.UserNotFoundException;
import org.example.infrastructure.configuration.google.GoogleCredentialsProvider;
import org.example.infrastructure.configuration.properties.GoogleCalendarProperties;
import org.example.infrastructure.repository.GoogleOAuthStateRepository;
import org.example.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private final UserRepository userRepository;

    private final GoogleOAuthStateRepository googleOAuthStateRepository;
    private final GoogleCalendarProperties properties;

    private final GoogleCredentialsProvider credentialsProvider;

    public String getAuthorizationUrl(String userEmail) {
        try {
            User user =
                    userRepository
                            .findByEmail(userEmail)
                            .orElseThrow(() -> new UserNotFoundException("User not found: "
                                    + userEmail));

            String state = UUID.randomUUID().toString();

            googleOAuthStateRepository.save(
                    new GoogleOAuthState(
                            state, user, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10)));

            GoogleClientSecrets clientSecrets = credentialsProvider.loadClientSecrets();

            GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(),
                            JSON_FACTORY,
                            clientSecrets,
                            Collections.singleton(CalendarScopes.CALENDAR))
                            .setAccessType("offline")
                            .build();

            AuthorizationCodeRequestUrl authorization =
                    flow.newAuthorizationUrl()
                            .setRedirectUri(properties.getRedirectUri())
                            .setState(state)
                            .set("prompt", "consent");

            return authorization.build();

        } catch (IOException | GeneralSecurityException exception) {
            throw new IllegalStateException("Failed to build Google OAuth URL! ", exception);
        }
    }

    public void exchangeCodeForRefreshToken(String code, String state) {

        try {
            GoogleOAuthState googleOAuthState =
                    googleOAuthStateRepository
                            .findById(state)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid OAuth state"));

            if (googleOAuthState.getExpiresAt().isBefore(LocalDateTime.now())) {

                googleOAuthStateRepository.delete(googleOAuthState);
                throw new IllegalStateException("OAuth state expired!");
            }

            User user = googleOAuthState.getUser();

            GoogleClientSecrets clientSecrets = credentialsProvider.loadClientSecrets();

            GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(),
                            JSON_FACTORY,
                            clientSecrets,
                            Collections.singleton(CalendarScopes.CALENDAR))
                            .setAccessType("offline")
                            .build();

            GoogleTokenResponse tokenResponse =
                    flow.newTokenRequest(code).setRedirectUri(
                            properties.getRedirectUri()).execute();

            String refreshToken = tokenResponse.getRefreshToken();

            if (refreshToken != null) {
                user.setGoogleRefreshToken(refreshToken);
                userRepository.save(user);
            }

            googleOAuthStateRepository.delete(googleOAuthState);

        } catch (IOException | GeneralSecurityException exception) {
            throw new IllegalStateException("Failed to exchange code for refresh token", exception);
        }
    }

    public void disconnect(String userEmail) {

        userRepository
                .findByEmail(userEmail)
                .ifPresent(
                        user -> {
                            user.setGoogleRefreshToken(null);
                            userRepository.save(user);
                        });
    }
}
