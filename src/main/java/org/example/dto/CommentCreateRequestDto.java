package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentCreateRequestDto(

        @NotNull(message = "{comment.taskId.notNull")
        Long taskId,
        @NotBlank(message = "{comment.text.notBlank}")
        String text
) {
}
