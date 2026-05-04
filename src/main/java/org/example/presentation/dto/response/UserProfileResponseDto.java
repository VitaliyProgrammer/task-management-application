package org.example.presentation.dto.response;

import java.util.Set;

public record UserProfileResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Set<String> roles,
        String emailUsername,
        String emailPassword,
        Long telegramChatId) {
}
