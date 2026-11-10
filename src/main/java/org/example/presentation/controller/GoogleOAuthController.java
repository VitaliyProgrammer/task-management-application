package org.example.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.infrastructure.integration.GoogleOAuthService;
import org.example.presentation.dto.response.GoogleAuthUrlResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/oauth/google-calendar")
@RequiredArgsConstructor
@Tag(
        name = "Google Calendar API",
        description = "Connect and disconnect the Google Calendar of the current user")
public class GoogleOAuthController {

    private static final String PROFILE_PAGE = "/profile?calendar=";

    private final GoogleOAuthService googleOAuthService;

    @Value("${app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    @GetMapping("/connect")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Connect Google Calendar",
            description = "Returns the Google OAuth2 URL the browser should open")
    public GoogleAuthUrlResponseDto connect(Authentication authentication) {

        String url = googleOAuthService.getAuthorizationUrl(authentication.getName());

        return new GoogleAuthUrlResponseDto(url);
    }

    @GetMapping("/callback")
    @Operation(
            summary = "Google OAuth callback",
            description = "Stores the refresh token and redirects back to the profile page")
    public ResponseEntity<Void> callback(@RequestParam String code, @RequestParam String state) {

        String result = "connected";

        try {
            googleOAuthService.exchangeCodeForRefreshToken(code, state);
        } catch (RuntimeException exception) {
            log.warn("Google Calendar connection failed: {}", exception.getMessage());
            result = "error";
        }

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(frontendUrl + PROFILE_PAGE + result))
                .build();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Disconnect Google Calendar",
            description = "Removes the Google Calendar integration of the current user")
    public void disconnect(Authentication authentication) {

        googleOAuthService.disconnect(authentication.getName());
    }
}
