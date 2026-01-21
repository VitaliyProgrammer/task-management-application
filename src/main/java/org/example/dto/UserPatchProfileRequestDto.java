package org.example.dto;

public record UserPatchProfileRequestDto(
        String email,
        String firstName,
        String lastName,
        String emailUsername,
        String emailPassword,
        Long telegramChatId,
        String telegramBotToken
) {
}
