package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserPatchProfileRequestDto;
import org.example.dto.UserProfileResponseDto;
import org.example.dto.UserPutProfileRequestDto;
import org.example.dto.UserUpdateRoleRequestDto;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Operations related to users: profile management")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user by ID",
            description = "Retrieve detailed about a specific user by their unique identifier. ")
    public UserProfileResponseDto getUserProfile(Authentication authentication) {

        return userService.getUserProfile(authentication.getName());
    }

    @PutMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update user profile",
            description = "Completely update personal user information")
    public UserProfileResponseDto updateUserProfile(
            Authentication authentication,
            @RequestBody @Valid UserPutProfileRequestDto request) {

        return userService.updateUserProfile(authentication.getName(), request);
    }

    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update user profile",
            description = "Partially update personal user information")
    public UserProfileResponseDto patchUserProfile(
            Authentication authentication,
            @RequestBody UserPatchProfileRequestDto request) {

        return userService.patchUserProfile(authentication.getName(), request);
    }

    @PutMapping("/{id}/role")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change user role", description = "Assign a new role to a user")
    public UserProfileResponseDto updateUserRole(
            @PathVariable Long id, @RequestBody @Valid UserUpdateRoleRequestDto request) {

        return userService.updateUserRole(id, request.role());
    }
}
