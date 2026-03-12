package org.example.domain.event;

import lombok.NonNull;
import org.example.domain.entity.Task;
import org.example.domain.entity.User;

public record SendEmailTaskEvent(
        @NonNull
        Task task,
        @NonNull
        User senderUser,
        String recipientEmail
) {
}
