package org.example.infrastructure.configuration.dropbox;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dropbox")
public record DropBoxProperties(String accessToken) {
}
