package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LabelCreateRequestDto(
        @NotBlank(message = "{label.name.notBlank}")
        String name,

        @NotBlank(message = "{label.color.notBlank}")
        String color,

        @Size(max = 255)
        String description
) {
}
