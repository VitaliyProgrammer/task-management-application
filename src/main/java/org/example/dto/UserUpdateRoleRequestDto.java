package org.example.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRoleRequestDto(
        @NotBlank(message = "{role.notBlank}")
        String role
) {
}
