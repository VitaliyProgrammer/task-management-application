package org.example.configuration.dropbox;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dropbox")
public record DropBoxProperties(String accessToken) {

}
