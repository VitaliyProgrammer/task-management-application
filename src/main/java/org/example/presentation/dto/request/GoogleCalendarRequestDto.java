package org.example.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record GoogleCalendarRequestDto(

        @NotNull(message = "{calendar.startDate.notNull}")
        LocalDate startDate,

        @NotNull(message = "{calendar.endDate.notNull}")
        LocalDate endDate
) {
}
