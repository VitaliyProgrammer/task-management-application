package org.example.infrastructure.integration;

import java.time.LocalDate;

public record TaskNotificationContent(
        String projectName,
        String title,
        String description,
        LocalDate dueDate,
        String priority,
        String status,
        String assignedByEmail
) {
}
