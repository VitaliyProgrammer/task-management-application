package org.example.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.v3.oas.annotations.media.Schema;

public record EmailCredentialsRequestDto(
        String emailUsername,

        @Schema(description = "Application password for SMTP", format = "password")
        @JsonProperty(access = Access.WRITE_ONLY)
        String applicationPassword) {
}
