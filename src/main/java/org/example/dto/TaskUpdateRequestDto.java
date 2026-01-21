package org.example.dto;

import java.time.LocalDate;
import java.util.Set;
import org.example.entity.status.TaskPriority;
import org.example.entity.status.TaskStatus;

public record TaskUpdateRequestDto(
        String name,
        String description,
        TaskPriority taskPriority,
        TaskStatus taskStatus,
        LocalDate dueDate,
        Long assigneeId,

        Set<Long> labelIds
) {
}
