package org.example.presentation.dto.response;

import java.time.Instant;

public record EmailResponseDto(
        Long id,
        String recipientEmail,
        Instant shippingTime
) {
}
