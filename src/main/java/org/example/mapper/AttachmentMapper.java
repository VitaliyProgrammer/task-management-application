package org.example.mapper;

import org.example.dto.AttachmentResponseDto;
import org.example.entity.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    @Mapping(target = "taskId", source = "task.id")
    AttachmentResponseDto toDto(Attachment attachment);
}
