package org.example.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRoleRequestDto(@NotBlank(message = "{role.notBlank}") String role) {
}
