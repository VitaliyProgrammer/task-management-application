package org.example.application.mapper;

import org.example.domain.entity.Attachment;
import org.example.presentation.dto.response.AttachmentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    @Mapping(target = "taskId", source = "task.id")
    AttachmentResponseDto toDto(Attachment attachment);
}
