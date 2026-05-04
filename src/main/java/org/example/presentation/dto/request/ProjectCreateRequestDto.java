package org.example.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record ProjectCreateRequestDto(
        @NotBlank(message = "{project.name.notBlank}")
        String name,
        @NotBlank(message = "{project.description.notBlank}")
        String description,
        @NotBlank(message = "{project.startDate.notNull}")
        LocalDate startDate,
        @NotBlank(message = "{project.endDate.notNull}")
        LocalDate endDate) {
}
