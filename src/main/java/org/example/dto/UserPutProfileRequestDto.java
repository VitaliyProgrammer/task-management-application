package org.example.dto;

import jakarta.validation.constraints.NotBlank;

public record UserPutProfileRequestDto(
        @NotBlank(message = "{firstName.notBlank}")
        String firstName,
        @NotBlank(message = "{lastName.notBlank}")
        String lastName
) {
}
