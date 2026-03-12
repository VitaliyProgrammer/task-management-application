package org.example.infrastructure.configuration.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.event.SendEmailTaskEvent;
import org.example.infrastructure.integration.EmailNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationTaskListener {

    private final EmailNotificationService emailNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskCreated(SendEmailTaskEvent event) {

        emailNotificationService.sendTaskCreatedEmail(event.task(),
                event.senderUser(), event.recipientEmail());
    }
}
