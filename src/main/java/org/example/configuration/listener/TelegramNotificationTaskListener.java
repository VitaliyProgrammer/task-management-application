package org.example.configuration.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.event.TaskCreatedEvent;
import org.example.service.integration.TelegramNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramNotificationTaskListener {

    private final TelegramNotificationService telegramNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskCreated(TaskCreatedEvent event) {

        telegramNotificationService.sendTaskCreatedNotification(event.task());
    }
}
