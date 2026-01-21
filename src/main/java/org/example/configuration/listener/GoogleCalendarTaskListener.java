package org.example.configuration.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Task;
import org.example.event.TaskCreatedEvent;
import org.example.service.integration.GoogleCalendarService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleCalendarTaskListener {
    private final GoogleCalendarService googleCalendarService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskCreated(TaskCreatedEvent event) {

        Task task = event.task();

        log.debug("Google Calendar listener triggered for task id={}", task.getId());

        googleCalendarService.createTaskEvent(task);
    }
}
