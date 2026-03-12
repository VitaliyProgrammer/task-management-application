package org.example.presentation.dto.request;

public record UserPatchProfileRequestDto(
        String email,
        String password,
        String firstName,
        String lastName,
        String emailUsername,
        String emailPassword,
        Long telegramChatId) {
}
