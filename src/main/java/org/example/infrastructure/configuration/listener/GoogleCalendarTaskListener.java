package org.example.infrastructure.configuration.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.event.SendGoogleCalendarTaskEvent;
import org.example.infrastructure.integration.GoogleCalendarNotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarTaskListener {
    private final GoogleCalendarNotificationService googleCalendarNotificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSendToGoogleCalendar(SendGoogleCalendarTaskEvent event) {

        log.debug("Google Calendar listener triggered for task id={}", event.task());

        googleCalendarNotificationService.createTaskEvent(event.task(),
                event.senderUser(), event.startDate(), event.endDate());
    }
}
