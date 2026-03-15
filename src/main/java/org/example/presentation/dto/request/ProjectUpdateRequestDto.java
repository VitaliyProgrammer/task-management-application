package org.example.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ProjectUpdateRequestDto(
        @NotBlank(message = "{project.name.notBlank}")
        String name,
        String description,
        @NotNull(message = "{project.startDate.notNull}")
        LocalDate startDate,
        @NotNull(message = "{project.endDate.notNull}")
        LocalDate endDate
) {
}
