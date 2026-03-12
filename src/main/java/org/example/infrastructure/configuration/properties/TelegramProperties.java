package org.example.infrastructure.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "telegram")
public class TelegramProperties {

    private String botToken;
    private boolean enabled = true;
}
