package org.example.presentation.dto.response;

public record UserRegistrationResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
