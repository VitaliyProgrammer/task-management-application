package org.example.service;

import org.example.dto.CommentCreateRequestDto;
import org.example.dto.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    CommentResponseDto create(CommentCreateRequestDto request);

    Page<CommentResponseDto> findCommentsByTaskId(Long taskId, Pageable page);
}
