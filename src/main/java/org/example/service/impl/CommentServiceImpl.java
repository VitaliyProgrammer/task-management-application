package org.example.service.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.dto.CommentCreateRequestDto;
import org.example.dto.CommentResponseDto;
import org.example.entity.Comment;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.exception.TaskNotFoundException;
import org.example.mapper.CommentMapper;
import org.example.repository.CommentRepository;
import org.example.repository.TaskRepository;
import org.example.security.AuthenticationUtil;
import org.example.service.CommentService;
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

        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new TaskNotFoundException(
                        "Task not found with id!: " + request.taskId()));

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
        return commentRepository.getAllByTaskId(taskId, pageable)
                .map(commentMapper::toDto);
    }
}
