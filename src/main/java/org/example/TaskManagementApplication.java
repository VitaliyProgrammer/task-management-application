package org.example;

import org.example.configuration.properties.EmailProperties;
import org.example.configuration.properties.GoogleCalendarProperties;
import org.example.configuration.properties.TelegramProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({GoogleCalendarProperties.class,
        EmailProperties.class, TelegramProperties.class})
public class TaskManagementApplication {
    public static void main(String[] args) {

        SpringApplication.run(TaskManagementApplication.class, args);
    }
}
