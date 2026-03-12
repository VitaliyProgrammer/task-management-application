package org.example.infrastructure.integration;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.example.domain.entity.Task;
import org.example.domain.entity.User;
import org.example.domain.entity.status.TaskPriority;
import org.example.domain.entity.status.TaskStatus;
import org.example.domain.exception.UserNotFoundException;
import org.example.infrastructure.configuration.google.GoogleCalendarFactory;
import org.example.infrastructure.configuration.properties.GoogleCalendarProperties;
import org.example.infrastructure.repository.UserRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoogleCalendarNotificationService {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    private final GoogleCalendarFactory googleCalendar;

    private final GoogleCalendarProperties googleCalendarProperties;

    private final UserRepository userRepository;

    @Async("googleCalendarExecutor")
    public void createTaskEvent(Task task, User senderUser, LocalDate startDate, LocalDate endDate) {

        log.info("GoogleCalendar: start creating event for task id={}", task);

        if (!googleCalendarProperties.isEnabled()) {
            log.debug("Google Calendar: notifications are disabled!");
            return;
        }


        if (!shouldCreateCalendarEvent(task)) {
            log.debug("Google Calendar event skipped for task id={}", task);
            return;
        }

        if (task.getAssignee() == null) {
            log.warn("Task {} has no assignee. Calendar event skipped.", task);
            return;
        }

        User assignee = userRepository.findById(task.getAssignee().getId())
                .orElseThrow(() -> new UserNotFoundException("Assignee not found!"));

        try {
            Calendar calendar = googleCalendar.createForUser(assignee);

            Event event = buildEvent(task, senderUser, startDate, endDate);

            insertEventWithRetry(calendar, event);

            log.info("Google Calendar event created for task id={}", task.getId());
        } catch (IOException exception) {
            log.error("Failed to create Google Calendar event for task id={}",
                    task.getId(), exception);
        }
    }

    private Event buildEvent(Task task, User senderUser, LocalDate startDate, LocalDate endDate) {

        DateTime start = buildStartDate(startDate);
        DateTime end = buildEndDate(endDate);

        TaskNotificationContent content = buildNotificationContent(task, senderUser);

        String description = buildDescription(content);

        Map<String, String> properties = taskNotificationContentToMap(content);

        return new Event()
                .setSummary(content.title())
                .setDescription(description)
                .setStart(new EventDateTime().setDateTime(start))
                .setEnd(new EventDateTime().setDateTime(end))
                .setColorId(mapPriorityToColor(task.getTaskPriority()))
                .setExtendedProperties(new Event.ExtendedProperties().setPrivate(properties));
    }

    private Map<String, String> taskNotificationContentToMap(TaskNotificationContent content) {

        Map<String, String> map = new HashMap<>();
        map.put("projectName", content.projectName());
        map.put("title", content.title());
        map.put("description", content.description());
        map.put("dueDate", content.dueDate() != null ? content.dueDate().toString() : "N/A");
        map.put("priority", content.priority());
        map.put("status", content.status());
        map.put("assignedByEmail", content.assignedByEmail());
        return map;
    }

    @Retryable(
            retryFor = {IOException.class, HttpResponseException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2))
    protected void insertEventWithRetry(Calendar calendar, Event event) throws IOException {

        Event createdEvent = calendar.events()
                .insert("primary", event)
                .execute();
        log.info("Event created: {}", createdEvent.getHtmlLink());
    }

    private DateTime buildStartDate(LocalDate startDate) {

        return new DateTime(startDate.atStartOfDay(ZONE_ID)
                .toInstant().toEpochMilli());
    }

    private DateTime buildEndDate(LocalDate endDate) {

        return new DateTime(endDate.atTime(23, 59)
                .atZone(ZONE_ID).toInstant().toEpochMilli());
    }

    private boolean shouldCreateCalendarEvent(Task task) {

        Long assigneeId = task.getAssignee() != null ? task.getAssignee().getId() : null;

        if (assigneeId == null) {
            log.warn("Email: task {} has no assignee", task.getId());
            return false;
        }

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new UserNotFoundException("Assignee not found!"));

        if (assignee == null || assignee.getGoogleRefreshToken() == null) {

            log.warn("Google Calendar: user {} has no refresh token ", assigneeId);
            return false;
        }
        return true;
    }

    private TaskNotificationContent buildNotificationContent(Task task, User senderUser) {

        return new TaskNotificationContent(
                task.getProject().getName(),
                task.getTitle(),
                task.getDescription() != null ? task.getDescription() : "N/A",
                task.getDueDate(),
                mapPriorityToColor(task.getTaskPriority()),
                mapStatusToColor(task.getTaskStatus()),
                senderUser.getEmail()
        );
    }


    private String buildDescription(TaskNotificationContent content) {

        return """
                📌 You have a new task assigned!
                                    
                🏗️ Project: %s
                📁 Title: %s
                📝 Description: %s
                📅 Due date: %s
                🚨 Priority: %s
                📊 Status: %s
                👤 Assigned by: %s
                """.formatted(
                content.projectName(),
                content.title(),
                content.description(),
                content.dueDate(),
                content.priority(),
                content.status(),
                content.assignedByEmail()
        );
    }

    private String mapPriorityToColor(TaskPriority priority) {

        if (priority == null) {
            return "1";
        }

        return switch (priority) {
            case HIGH -> "11";
            case MEDIUM -> "5";
            case LOW -> "2";
        };
    }

    private String mapStatusToColor(TaskStatus status) {

        return switch (status) {
            case NOT_STARTED -> "🔴 NOT_STARTED";
            case IN_PROGRESS -> "🟠 IN_PROGRESS";
            case COMPLETED -> "🟢 COMPLETED";
        };
    }
}
