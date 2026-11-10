package org.example.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.infrastructure.integration.UserCredentialsExternalService;
import org.example.infrastructure.security.UserSecurity;
import org.example.presentation.dto.request.EmailCredentialsRequestDto;
import org.example.presentation.dto.request.TelegramRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@Tag(
        name = "User integrations",
        description = "Connect Email (SMTP) and Telegram of the current user")
public class UserIntegrationController {

    private final UserCredentialsExternalService credentialsService;

    @PutMapping("/email-credentials")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Save email credentials",
            description = "Stores encrypted SMTP credentials used to send task notifications")
    public void saveEmailCredentials(
            @AuthenticationPrincipal UserSecurity currentUser,
            @RequestBody @Valid EmailCredentialsRequestDto request) {

        credentialsService.setEmailCredentials(
                currentUser.getId(), request.emailUsername(), request.applicationPassword());
    }

    @PutMapping("/telegram")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Save Telegram chat ID",
            description = "Stores the Telegram chat ID used to send task notifications")
    public void saveTelegramChatId(
            @AuthenticationPrincipal UserSecurity currentUser,
            @RequestBody @Valid TelegramRequestDto request) {

        credentialsService.setTelegramCredentials(currentUser.getId(), request.telegramChatId());
    }
}
