package org.example;

import org.example.infrastructure.configuration.properties.EmailProperties;
import org.example.infrastructure.configuration.properties.GoogleCalendarProperties;
import org.example.infrastructure.configuration.properties.TelegramProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableRetry
@EnableScheduling
@EnableConfigurationProperties({
        GoogleCalendarProperties.class,
        EmailProperties.class,
        TelegramProperties.class
})
public class TaskManagementApplication {
    public static void main(String[] args) {

        SpringApplication.run(TaskManagementApplication.class, args);
    }
}
