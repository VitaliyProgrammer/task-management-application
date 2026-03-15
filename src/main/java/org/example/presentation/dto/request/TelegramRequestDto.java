package org.example.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record TelegramRequestDto(
        @NotNull(message = "{telegram.chatId.notNull}")
        Long telegramChatId
) {
}
