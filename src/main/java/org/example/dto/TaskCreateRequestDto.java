package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;
import org.example.entity.status.TaskPriority;
import org.example.entity.status.TaskStatus;

public record TaskCreateRequestDto(
        @NotBlank(message = "{task.name.notBlank}")
        String title,
        String description,
        @NotNull(message = "{task.priority.notNull}")
        TaskPriority taskPriority,
        @NotNull(message = "{task.status.notNull}")
        TaskStatus taskStatus,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dueDate,
        @NotNull(message = "{task.projectId.notNull}")
        Long projectId,
        Long assigneeId,
        Set<Long> labelIds
) {
}
