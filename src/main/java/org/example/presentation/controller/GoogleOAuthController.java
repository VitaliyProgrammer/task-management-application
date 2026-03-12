package org.example.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.application.service.TaskService;
import org.example.infrastructure.integration.GoogleOAuthService;
import org.example.presentation.dto.request.GoogleCalendarRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth/google-calendar")
@RequiredArgsConstructor
@Tag(name = "",
        description = "")
public class GoogleOAuthController {

    private final GoogleOAuthService googleOAuthService;

    private final TaskService taskService;

    @GetMapping("/connect")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Connect Google Calendar",
            description = "Returns Google OAuth2 authentication URL for the current user")
    public String connect(Authentication authentication) {

        return googleOAuthService.getAuthorizationUrl(authentication.getName());
    }

    @PostMapping("/sent-google-calendar")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Create Google Calendar event from Task",
            description = "Creates a Google Calendar event using TaskCreatedEvent logic")
    public void createEventFromTask(@RequestBody GoogleCalendarRequestDto request) {

        taskService.sendNotificationToGoogleCalendar(request);
    }

    @GetMapping("/callback")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Google OAuth callback",
            description = "Handles Google OAuth callback and stores refresh token")
    public void callback(@RequestParam String code, @RequestParam String state) {
        System.out.println("CALLBACK CALLED !!!");
        googleOAuthService.exchangeCodeForRefreshToken(code, state);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary =
                    "Disconnect Google Calendar,"
                            + "Removes Google Calendar integration for current user")
    public void disconnect(Authentication authentication) {

        googleOAuthService.disconnect(authentication.getName());
    }
}
