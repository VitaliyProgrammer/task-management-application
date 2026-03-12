package org.example.presentation.dto.response;

import java.time.Instant;

public record TelegramResponseDto(
        Long taskId,
        Long chatId,
        Instant sentAt
) {
}
