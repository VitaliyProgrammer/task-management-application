package org.example.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.application.service.TaskService;
import org.example.presentation.dto.request.EmailRequestDto;
import org.example.presentation.dto.request.GoogleCalendarRequestDto;
import org.example.presentation.dto.response.EmailResponseDto;
import org.example.presentation.dto.response.GoogleCalendarResponseDto;
import org.example.presentation.dto.response.TelegramResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks/{taskId}/notifications")
@RequiredArgsConstructor
@Tag(
        name = "Task notifications",
        description = "Send a task to Email, Telegram or Google Calendar")
public class TaskNotificationController {

    private final TaskService taskService;

    @PostMapping("/email")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Send task by email",
            description = "Sends the task to the given email address using the sender SMTP account")
    public EmailResponseDto sendEmail(
            @PathVariable Long taskId,
            @RequestBody @Valid EmailRequestDto request) {

        return taskService.sendNotificationToEmail(taskId, request);
    }

    @PostMapping("/telegram")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Send task to Telegram",
            description = "Sends the task to the Telegram chat of its assignee")
    public TelegramResponseDto sendTelegram(@PathVariable Long taskId) {

        return taskService.sendNotificationToTelegram(taskId);
    }

    @PostMapping("/calendar")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(
            summary = "Add task to Google Calendar",
            description = "Creates an event in the Google Calendar of the current user")
    public GoogleCalendarResponseDto addToCalendar(
            @PathVariable Long taskId,
            @RequestBody @Valid GoogleCalendarRequestDto request) {

        return taskService.sendNotificationToGoogleCalendar(taskId, request);
    }
}
