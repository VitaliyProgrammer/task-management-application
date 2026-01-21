package org.example.dto;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDto(
        @NotBlank(message = "{userName.notBlank}")
        String email,
        @NotBlank(message = "{password.notBlank}")
        String password
) { }

