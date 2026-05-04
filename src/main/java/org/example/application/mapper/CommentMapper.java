package org.example.application.mapper;

import org.example.domain.entity.Comment;
import org.example.presentation.dto.response.CommentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "userId", source = "user.id")
    CommentResponseDto toDto(Comment comment);
}
