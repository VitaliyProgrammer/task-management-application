package org.example.application.mapper;

import org.example.domain.entity.Project;
import org.example.presentation.dto.request.ProjectCreateRequestDto;
import org.example.presentation.dto.response.ProjectResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Project toEntity(ProjectCreateRequestDto request);

    @Mapping(target = "status", expression = "java(project.getStatus().name())")
    ProjectResponseDto toDto(Project project);
}
