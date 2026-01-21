package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.example.dto.AttachmentResponseDto;
import org.example.service.AttachmentService;
import org.example.service.DropBoxService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/attachments")
@RequiredArgsConstructor
@Tag(name = "Attachments",
        description = "API for managing file attachments linked to tasks via DropBox")
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final DropBoxService dropBoxService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Upload attachment to task",
            description = "Uploads a file and attaches it to a specific task")
    public AttachmentResponseDto uploadFile(@RequestParam Long taskId,
                                            @RequestPart MultipartFile file) throws IOException {

        return attachmentService.uploadFile(taskId, file);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get attachments for task",
            description = "Returns a paginated list of all attachments linked to a specific task")
    public Page<AttachmentResponseDto> getAttachmentsByTaskId(
            @RequestParam Long taskId, Pageable pageable) {

        return attachmentService.findAttachmentsByTaskId(taskId, pageable);
    }
}
