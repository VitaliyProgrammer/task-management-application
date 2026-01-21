package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record AttachmentResponseDto(
        Long id,
        Long taskId,
        String dropBoxFieldId,
        String filename,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime uploadDate
) {
}
