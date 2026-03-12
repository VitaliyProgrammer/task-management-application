package org.example.presentation.dto.request;

import java.time.LocalDate;
import java.util.Set;
import org.example.domain.entity.status.TaskPriority;
import org.example.domain.entity.status.TaskStatus;

public record TaskUpdateRequestDto(
        String name,
        String description,
        TaskPriority taskPriority,
        TaskStatus taskStatus,
        LocalDate dueDate,
        Long assigneeId,
        Set<Long> labelIds) {
}
