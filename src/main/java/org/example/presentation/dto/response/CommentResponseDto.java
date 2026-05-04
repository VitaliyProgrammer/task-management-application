package org.example.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record CommentResponseDto(
        Long id,
        Long taskId,
        Long userId,
        String text,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp) {
}
