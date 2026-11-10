package org.example.application.mapper;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

import org.example.domain.entity.Project;
import org.example.presentation.dto.request.ProjectCreateRequestDto;
import org.example.presentation.dto.request.ProjectUpdateRequestDto;
import org.example.presentation.dto.response.ProjectResponseDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Project toEntity(ProjectCreateRequestDto request);

    @Mapping(target = "status", expression = "java(project.getStatus().name())")
    ProjectResponseDto toDto(Project project);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = IGNORE)
    void updateProjectFromDto(ProjectUpdateRequestDto dto, @MappingTarget Project project);
}
