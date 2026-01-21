package org.example.service.integration;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import java.io.IOException;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.configuration.google.GoogleCalendarFactory;
import org.example.entity.Task;
import org.example.entity.User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleCalendarService {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    private final GoogleCalendarFactory googleCalendar;

    public void createTaskEvent(Task task) {

        if (!shouldCreateCalendarEvent(task)) {
            log.debug("Google Calendar event skipped for task id={}", task.getId());
            return;
        }

        try {
            Calendar calendar = googleCalendar.createForUser(task.getAssignee());

            Event event = buildEvent(task);

            calendar.events().insert("primary", event).execute();

            log.info("Google Calendar event created for task id={}", task.getId());
        } catch (IOException exception) {
            log.error("Failed to create Google Calendar event got task id={}",
                    task.getId(), exception);
        }
    }

    private boolean shouldCreateCalendarEvent(Task task) {

        User assignee = task.getAssignee();

        return task.getDueDate() != null && assignee != null
                && assignee.getGoogleRefreshToken() != null;
    }

    private Event buildEvent(Task task) {

        DateTime start = new DateTime(
                task.getDueDate()
                        .atStartOfDay(ZONE_ID)
                        .toInstant()
                        .toEpochMilli()
        );

        DateTime end = new DateTime(
                task.getDueDate()
                        .atTime(23, 59)
                        .atZone(ZONE_ID)
                        .toInstant()
                        .toEpochMilli()
        );

        return new Event()
                .setSummary(task.getTitle())
                .setDescription(task.getDescription())
                .setStart(new EventDateTime().setDateTime(start))
                .setEnd(new EventDateTime().setDateTime(end));
    }
}
