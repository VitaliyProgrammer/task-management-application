package org.example.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import org.example.domain.entity.status.TaskPriority;
import org.example.domain.entity.status.TaskStatus;

public record TaskUpdateRequestDto(
        @NotBlank(message = "{task.title.notBlank}")
        String name,
        String description,
        @NotNull(message = "{task.priority.notNull}")
        TaskPriority taskPriority,
        @NotNull(message = "{task.status.notNull}")
        TaskStatus taskStatus,
        @NotNull(message = "{task.dueDate.notNull}")
        LocalDate dueDate,
        @NotNull(message = "{task.assigneeId.notNull}")
        Long assigneeId,
        Set<Long> labelIds) {
}
