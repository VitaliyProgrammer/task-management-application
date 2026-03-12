package org.example.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.application.service.TaskService;
import org.example.presentation.dto.request.TelegramRequestDto;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Telegram API",
        description = "Manage and sending notifications in Telegram")
public class UserTelegramController {

    private final TaskService taskService;

    @PostMapping("/{taskId}/send-telegram")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Link Telegram chat ID for notifications",
            description = "Stores Telegram chatId for sending task notifications")
    public TelegramResponseDto sendTelegramTask(
            @PathVariable Long taskId, @RequestBody @Valid TelegramRequestDto request) {

        return taskService.sendNotificationToTelegram(taskId, request);
    }
}
