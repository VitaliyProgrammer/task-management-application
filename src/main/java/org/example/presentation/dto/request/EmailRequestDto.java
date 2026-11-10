package org.example.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRequestDto(

        @NotBlank(message = "{email.notBlank}")
        @Email(message = "{email.invalid}")
        String recipientEmail
) {
}
