package org.example.dto;

public record LabelResponseDto(
        Long id,
        String name,
        String color,
        String description
) {
}
