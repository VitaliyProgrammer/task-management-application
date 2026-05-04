package org.example.application.service;

import org.example.presentation.dto.request.CommentCreateRequestDto;
import org.example.presentation.dto.response.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentResponseDto create(CommentCreateRequestDto request);

    Page<CommentResponseDto> findCommentsByTaskId(Long taskId, Pageable page);
}
