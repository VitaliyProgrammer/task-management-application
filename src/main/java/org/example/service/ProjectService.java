package org.example.service;

import org.example.dto.ProjectCreateRequestDto;
import org.example.dto.ProjectResponseDto;
import org.example.dto.ProjectUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {

    ProjectResponseDto create(ProjectCreateRequestDto request);

    ProjectResponseDto getById(Long id);

    Page<ProjectResponseDto> getAll(Pageable pageable);

    ProjectResponseDto update(Long id, ProjectUpdateRequestDto request);

    void delete(Long id);
}
