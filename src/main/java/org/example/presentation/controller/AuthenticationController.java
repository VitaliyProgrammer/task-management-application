package org.example.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.application.service.UserService;
import org.example.presentation.dto.request.UserLoginRequestDto;
import org.example.presentation.dto.request.UserRegistrationRequestDto;
import org.example.presentation.dto.response.UserLoginResponseDto;
import org.example.presentation.dto.response.UserRegistrationResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and authentication")
public class AuthenticationController {

    private final UserService userService;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user", description = "Creates a new account for user")
    public UserRegistrationResponseDto registration(
            @RequestBody @Valid UserRegistrationRequestDto request) {

        return userService.registration(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "User login",
            description =
                    "Authenticates a user using email and password, "
                            + "and returns a JWT access token")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto request) {
        return userService.login(request);
    }
}
