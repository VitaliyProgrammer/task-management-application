package org.example.mapper;

import org.example.dto.ProjectCreateRequestDto;
import org.example.dto.ProjectResponseDto;
import org.example.entity.Project;
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
