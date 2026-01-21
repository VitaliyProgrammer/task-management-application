package org.example.service.integration;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.configuration.properties.TelegramProperties;
import org.example.entity.Task;
import org.example.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramNotificationService {

    private final TelegramProperties telegramProperties;
    private final RestTemplate restTemplate;

    public void sendTaskCreatedNotification(Task task) {

        if (!telegramProperties.isEnabled()) {
            log.debug("Telegram notifications is disabled!");
            return;
        }

        User assignee = task.getAssignee();

        if (assignee == null) {
            log.warn("Telegram message skipped: task id={} has no assignee",
                    task.getId());
            return;
        }

        Long chatId = assignee.getTelegramChatId();
        String botToken = assignee.getTelegramBotToken();

        if (chatId == null || botToken == null || botToken.isBlank()) {
            log.warn("Telegram message skipped: user id={} missing chatId or botToken",
                    assignee.getId());
            return;
        }
        
        String messageText = buildTelegramTextBody(task);

        String url = String.format(
                "https://api.telegram.org/bot%s/sendMessage",
                telegramProperties.getBotToken()
        );

        Map<String, Object> payload = Map.of(
                "chat_id", assignee.getTelegramChatId(),
                "text", messageText
        );

        try {
            restTemplate.postForObject(url, payload, String.class);
            log.info("Telegram sent: task id={} to chatId={}", task.getId(), chatId);
        } catch (Exception exception) {
            log.warn("Failed to send Telegram for task id={}", task.getId(), exception);
        }
    }

    private String buildTelegramTextBody(Task task) {

        List<String> textBody = List.of(
                "!Hi!",
                "",
                "New task assigned!",
                "",
                "Title: " + task.getTitle(),
                "Description: " + task.getDescription(),
                "Due date: " + task.getDueDate(),
                "Priority: " + task.getTaskPriority()
        );

        StringBuilder body = new StringBuilder();

        for (String line : textBody) {
            body.append(line).append("\n");
        }

        return body.toString();
    }
}
