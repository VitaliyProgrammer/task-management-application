package org.example.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "email")
public class EmailProperties {

    private boolean enabled = true;
    private String host = "smtp.gmail.com";
    private int port = 587;
    private boolean authentication = true;
    private boolean starttls = true;
}
