package org.example.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.application.service.TaskService;
import org.example.infrastructure.integration.UserCredentialsExternalService;
import org.example.presentation.dto.request.EmailCredentialsRequestDto;
import org.example.presentation.dto.request.EmailRequestDto;
import org.example.presentation.dto.response.EmailResponseDto;
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
@Tag(name = "Email API",
        description = "Manage and sending notifications in Email")
public class UserEmailController {

    private final UserCredentialsExternalService emailCredentialsService;

    private final TaskService taskService;

    @PostMapping("/{id}/email-credentials")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("#id == authentication.principal.id")
    @Operation(
            summary = "Set email credentials for notifications",
            description = "Stores encrypted SMTP-credentials used for sending task notifications")
    public void setEmailCredentials(
            @PathVariable Long id, @RequestBody @Valid EmailCredentialsRequestDto request) {
        emailCredentialsService.setEmailCredentials(
                id, request.emailUsername(), request.applicationPassword());
    }

    @PostMapping("/{taskId}/send-mail")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Send task to email",
            description = "Send created task to email team lead or administrator to employee")
    public EmailResponseDto sendEmailTask(@PathVariable Long taskId,
                                          @RequestBody @Valid EmailRequestDto request) {

        return taskService.sendNotificationToEmail(taskId, request);
    }
}
