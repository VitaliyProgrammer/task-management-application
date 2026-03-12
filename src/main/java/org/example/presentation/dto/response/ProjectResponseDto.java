package org.example.presentation.dto.response;

import java.time.LocalDate;

public record ProjectResponseDto(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String status) {
}
