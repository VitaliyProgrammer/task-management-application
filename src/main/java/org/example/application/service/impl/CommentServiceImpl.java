package org.example.application.service.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.application.mapper.CommentMapper;
import org.example.application.service.CommentService;
import org.example.domain.entity.Comment;
import org.example.domain.entity.Task;
import org.example.domain.entity.User;
import org.example.domain.exception.TaskNotFoundException;
import org.example.infrastructure.repository.CommentRepository;
import org.example.infrastructure.repository.TaskRepository;
import org.example.infrastructure.security.AuthenticationUtil;
import org.example.presentation.dto.request.CommentCreateRequestDto;
import org.example.presentation.dto.response.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public CommentResponseDto create(CommentCreateRequestDto request) {

        User user = authenticationUtil.getCurrentUser();

        Task task =
                taskRepository
                        .findById(request.taskId())
                        .orElseThrow(
                                () -> new TaskNotFoundException("Task not found with id!: "
                                        + request.taskId()));

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setUser(user);
        comment.setText(request.text());
        comment.setTimestamp(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toDto(savedComment);
    }

    @Override
    public Page<CommentResponseDto> findCommentsByTaskId(Long taskId, Pageable pageable) {
        return commentRepository.getAllByTaskId(taskId, pageable).map(commentMapper::toDto);
    }
}
