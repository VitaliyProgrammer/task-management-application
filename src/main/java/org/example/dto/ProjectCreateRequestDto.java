package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record ProjectCreateRequestDto(
        @NotBlank(message = "{project.name.notBlank}")
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate
) {
}
