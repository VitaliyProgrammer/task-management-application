package org.example.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.application.service.AttachmentService;
import org.example.application.service.DropBoxService;
import org.example.presentation.dto.response.AttachmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/dropbox/")
@RequiredArgsConstructor
@Tag(
        name = "DropBox API",
        description = "API for managing file attachments linked to tasks via DropBox")
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final DropBoxService dropBoxService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(
            summary = "Upload attachment to DropBox",
            description = "Uploads files and attaches it to a specific task")
    public AttachmentResponseDto uploadFile(
            @RequestParam
            @Parameter(description = "ID of the task to attach the file to", required = true)
            Long taskId,
            @RequestPart
            @Parameter(description = "File to upload", required = true)
            MultipartFile file) throws IOException {

        return attachmentService.uploadFile(taskId, file);
    }

    @GetMapping("/download")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Download attachment from DropBox",
            description = "Downloads files from DropBox by its path or ID")
    public byte[] downloadFile(@RequestParam @Parameter(description = "Path to the file in DropBox",
            required = true) String dropBoxPath) {

        return dropBoxService.downloadFile(dropBoxPath);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(
            summary = "Get attachments for task",
            description = "Returns a paginated list of all attachments linked to a specific task")
    public Page<AttachmentResponseDto> getAttachmentsByTaskId(
            @RequestParam Long taskId, Pageable pageable) {

        return attachmentService.findAttachmentsByTaskId(taskId, pageable);
    }
}
