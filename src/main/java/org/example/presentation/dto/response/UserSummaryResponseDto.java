package org.example.presentation.dto.response;

import java.util.Set;

public record UserSummaryResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Set<String> roles,
        boolean telegramConnected,
        boolean emailConnected) {
}
