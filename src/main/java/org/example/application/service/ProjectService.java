package org.example.application.service;

import org.example.presentation.dto.request.ProjectCreateRequestDto;
import org.example.presentation.dto.request.ProjectUpdateRequestDto;
import org.example.presentation.dto.response.ProjectResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {

    ProjectResponseDto create(ProjectCreateRequestDto request);

    ProjectResponseDto getById(Long id);

    Page<ProjectResponseDto> getAll(Pageable pageable);

    ProjectResponseDto update(Long id, ProjectUpdateRequestDto request);

    void delete(Long id);
}
