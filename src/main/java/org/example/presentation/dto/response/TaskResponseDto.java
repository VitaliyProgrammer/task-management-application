package org.example.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import org.example.domain.entity.status.TaskPriority;
import org.example.domain.entity.status.TaskStatus;

public record TaskResponseDto(
        Long id,
        String title,
        String description,
        TaskPriority taskPriority,
        TaskStatus taskStatus,
        LocalDate dueDate,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        Long projectId,
        Long assigneeId,
        Set<LabelResponseDto> labels) {
}
