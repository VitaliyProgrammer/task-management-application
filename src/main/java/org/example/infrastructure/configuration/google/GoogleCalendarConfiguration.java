package org.example.infrastructure.configuration.google;

import org.example.infrastructure.configuration.properties.GoogleCalendarProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GoogleCalendarProperties.class)
public class GoogleCalendarConfiguration {
}
