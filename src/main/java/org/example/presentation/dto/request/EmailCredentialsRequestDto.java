package org.example.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record EmailCredentialsRequestDto(
        @NotBlank(message = "{email.notBlank}")
        String emailUsername,

        @NotBlank(message = "{password.notBlank}")
        @Schema(description = "Application password for SMTP", format = "password")
        @JsonProperty(access = Access.WRITE_ONLY)
        String applicationPassword
) {
}
