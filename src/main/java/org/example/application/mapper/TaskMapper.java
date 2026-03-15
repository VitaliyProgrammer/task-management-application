package org.example.application.mapper;

import org.example.domain.entity.Task;
import org.example.presentation.dto.request.TaskCreateRequestDto;
import org.example.presentation.dto.request.TaskUpdateRequestDto;
import org.example.presentation.dto.response.TaskResponseDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    Task toModel(TaskCreateRequestDto request);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "assigneeId", source = "assignee.id")
    TaskResponseDto toDto(Task task);

    @Mapping(target = "title", source = "name")
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateTaskFromDto(TaskUpdateRequestDto dto, @MappingTarget Task task);
}
