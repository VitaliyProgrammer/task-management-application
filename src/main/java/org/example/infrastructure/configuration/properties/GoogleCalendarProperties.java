package org.example.infrastructure.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "google.calendar")
public class GoogleCalendarProperties {
    private String credentialsPath;
    private String redirectUri;
    private boolean enabled = true;
}
