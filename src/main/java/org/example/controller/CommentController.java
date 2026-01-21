package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.CommentCreateRequestDto;
import org.example.dto.CommentResponseDto;
import org.example.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Endpoints for managing comments on task")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Create a new comment",
            description = "Creates a new comment for a specific task.")
    public CommentResponseDto create(@RequestBody @Valid CommentCreateRequestDto request) {
        return commentService.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get all comments for a task",
            description = "Returns a single comment by its identifier")
    public Page<CommentResponseDto> getAllCommentsByTaskId(
            @RequestParam Long taskId, Pageable pageable) {
        return commentService.findCommentsByTaskId(taskId, pageable);
    }
}
