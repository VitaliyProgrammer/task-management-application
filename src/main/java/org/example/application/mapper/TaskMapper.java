package org.example.application.mapper;

import org.example.domain.entity.Task;
import org.example.presentation.dto.request.TaskCreateRequestDto;
import org.example.presentation.dto.response.TaskResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "taskStatus", source = "taskStatus")
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    Task toModel(TaskCreateRequestDto request);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "assigneeId", source = "assignee.id")
    TaskResponseDto toDto(Task task);
}
