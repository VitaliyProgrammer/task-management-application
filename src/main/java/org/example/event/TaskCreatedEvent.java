package org.example.event;

import org.example.entity.Task;

public record TaskCreatedEvent(
        Task task
) {
}
