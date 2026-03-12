package org.example.domain.event;

import java.time.LocalDate;
import lombok.NonNull;
import org.example.domain.entity.Task;
import org.example.domain.entity.User;

public record SendGoogleCalendarTaskEvent(

        @NonNull
        Task task,
        @NonNull
        User senderUser,
        @NonNull LocalDate startDate,
        @NonNull LocalDate endDate
) {
}
