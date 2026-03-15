package org.example.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EmailRequestDto(
        @NotBlank(message = "{email.notBlank}")
        @NotBlank(message = "{email.invalid}")
        String recipientEmail
) {
}
