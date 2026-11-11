package org.example.infrastructure.configuration.dropbox;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Dropbox credentials.
 *
 * <p>Production uses a refresh token together with the app key and secret: the SDK exchanges
 * it for a short-lived access token whenever the current one is about to expire, so the
 * integration keeps working for months without manual steps.
 *
 * <p>A plain access token is still accepted for local development, but Dropbox issues it
 * for about four hours only.
 */
@ConfigurationProperties(prefix = "dropbox")
public record DropBoxProperties(
        String accessToken,
        String refreshToken,
        String appKey,
        String appSecret) {

    public boolean usesRefreshToken() {

        return hasText(refreshToken) && hasText(appKey) && hasText(appSecret);
    }

    private static boolean hasText(String value) {

        return value != null && !value.isBlank();
    }
}
