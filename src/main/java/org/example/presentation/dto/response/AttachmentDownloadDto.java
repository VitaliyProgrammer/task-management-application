package org.example.presentation.dto.response;

public record AttachmentDownloadDto(
        String filename,
        byte[] content) {
}
