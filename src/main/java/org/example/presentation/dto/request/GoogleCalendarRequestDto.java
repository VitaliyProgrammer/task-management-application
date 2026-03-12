package org.example.presentation.dto.request;

import java.time.LocalDate;

public record GoogleCalendarRequestDto(
        Long taskId,
        LocalDate startDate,
        LocalDate endDate
) {
}
