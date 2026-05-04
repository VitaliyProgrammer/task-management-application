package org.example.infrastructure.configuration.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.event.SendTelegramTaskEvent;
import org.example.infrastructure.integration.TelegramNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramNotificationTaskListener {

    private final TelegramNotificationService telegramNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskCreated(SendTelegramTaskEvent event) {

        log.info("[TELEGRAM][LISTENER] SendTelegramTaskEvent received. taskId={}",
                event.task().getId());

        telegramNotificationService.sendTaskCreatedNotification(
                event.task(), event.senderUser());
    }
}
