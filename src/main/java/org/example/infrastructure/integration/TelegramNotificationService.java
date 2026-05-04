package org.example.infrastructure.integration;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.Task;
import org.example.domain.entity.User;
import org.example.domain.entity.status.TaskPriority;
import org.example.domain.entity.status.TaskStatus;
import org.example.domain.exception.UserNotFoundException;
import org.example.infrastructure.configuration.properties.TelegramProperties;
import org.example.infrastructure.repository.UserRepository;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramNotificationService {

    private static final Semaphore TELEGRAM_RATE_LIMITER = new Semaphore(20);
    private final TelegramProperties telegramProperties;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    @Async("telegramExecutor")
    public void sendTaskCreatedNotification(Task task, User senderUser) {

        log.info("Telegram: start sending notification for task id={}", task.getId());

        if (!telegramProperties.isEnabled()) {
            log.info("Telegram: notifications is disabled!");
            return;
        }

        Optional<User> assignee = getAssigneeWithTelegramCredentials(task);

        if (assignee.isEmpty()) {
            return;
        }

        User user = assignee.get();

        Long chatId = user.getTelegramChatId();

        if (!TELEGRAM_RATE_LIMITER.tryAcquire()) {

            log.warn("Telegram: rate limit exceeded, skip sending for task {}", task.getId());
            return;
        }

        String url = "https://api.telegram.org/bot" + telegramProperties.getBotToken()
                + "/sendMessage";

        TaskNotificationContent content = buildNotificationContent(task, senderUser);

        Map<String, Object> payload = Map.of(
                "chat_id", chatId,
                "text", buildTelegramTextBody(content),
                "parse_mode", "HTML"
        );

        try {
            sendMessageWithRetry(url, payload);
            log.info("Telegram: message sent successfully to chatId={}", chatId);
        } catch (Exception exception) {
            log.error("Telegram: failed to send message for task id={}", task.getId(), exception);
        } finally {
            TELEGRAM_RATE_LIMITER.release();
        }
    }

    @Retryable(
            retryFor = {HttpClientErrorException.class, ResourceAccessException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2))
    public void sendMessageWithRetry(String url, Map<String, Object> payload) {

        restTemplate.postForObject(url, payload, String.class);
    }

    private Optional<User> getAssigneeWithTelegramCredentials(Task task) {

        Long assigneeId = task.getAssignee() != null ? task.getAssignee().getId() : null;

        if (assigneeId == null) {
            log.warn("Telegram: task {} has no assignee", task.getId());
            return Optional.empty();
        }

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new UserNotFoundException("Assignee not found!"));

        if (assignee.getTelegramChatId() == null) {

            log.warn("Telegram: assignee {} has no chatId for task {}", assigneeId, task.getId());
            return Optional.empty();
        }

        return Optional.of(assignee);
    }

    private TaskNotificationContent buildNotificationContent(Task task, User senderUser) {

        return new TaskNotificationContent(
                task.getProject() != null
                        ? normalizeField(task.getProject().getName()) : "N/A",
                task.getTitle() != null ? normalizeField(task.getTitle()) : "N/A",
                task.getDescription() != null ? normalizeField(task.getDescription()) : "N/A",
                task.getDueDate() != null ? task.getDueDate() : LocalDate.now(),
                formatPriorityForTelegram(task.getTaskPriority()),
                formatStatusForTelegram(task.getTaskStatus()),
                senderUser.getEmail()
        );
    }

    private String buildTelegramTextBody(TaskNotificationContent content) {

        return """
                <b>📌 You have a new task assigned!</b>

                <b>🏗️ Project:</b> %s
                <b>📁 Title:</b> %s
                <b>📝 Description:</b> %s
                <b>📅 Due date:</b> %s
                <b>🚨 Priority:</b> %s
                <b>📊 Status:</b> %s
                <b>👤 Assigned by:</b> %s
                """
                .formatted(
                        content.projectName(),
                        content.title(),
                        content.description(),
                        content.dueDate(),
                        content.priority(),
                        content.status(),
                        content.assignedByEmail()
                );
    }

    private String normalizeField(String value) {

        if (value == null || value.isBlank() || "string".equals(value)) {
            return "N/A";
        }
        return value;
    }

    private String formatPriorityForTelegram(TaskPriority priority) {

        if (priority == null || priority == TaskPriority.NO_SPECIFIED) {
            return "N/A";
        }

        return switch (priority) {
            case HIGH -> "🔴 HIGH";
            case MEDIUM -> "🟠 MEDIUM";
            case LOW -> "🟢 LOW";
            default -> "N/A";
        };
    }

    private String formatStatusForTelegram(TaskStatus status) {
        if (status == null || status == TaskStatus.NO_SPECIFIED) {
            return "N/A";
        }

        return switch (status) {
            case NOT_STARTED -> "⚪ NOT_STARTED";
            case IN_PROGRESS -> "🟠 IN_PROGRESS";
            case COMPLETED -> "🟢 COMPLETED";
            default -> "N/A";
        };
    }
}
