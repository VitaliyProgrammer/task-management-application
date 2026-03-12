package org.example.presentation.dto.response;

import java.time.LocalDate;

public record GoogleCalendarResponseDto(
        Long taskId,
        LocalDate startDate,
        LocalDate endDate
) {
}
