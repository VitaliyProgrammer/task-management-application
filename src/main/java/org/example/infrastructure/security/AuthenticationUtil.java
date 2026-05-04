package org.example.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.example.application.service.UserService;
import org.example.domain.entity.User;
import org.example.domain.exception.UserNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final UserService userService;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UserNotFoundException("No authenticated user found!");
        }

        String email = authentication.getName();

        return userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: "
                        + email));
    }
}
