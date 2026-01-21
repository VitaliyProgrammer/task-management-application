package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.ProjectCreateRequestDto;
import org.example.dto.ProjectResponseDto;
import org.example.dto.ProjectUpdateRequestDto;
import org.example.entity.Project;
import org.example.entity.status.ProjectStatus;
import org.example.exception.ProjectNotFoundException;
import org.example.mapper.ProjectMapper;
import org.example.repository.ProjectRepository;
import org.example.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponseDto create(ProjectCreateRequestDto request) {

        Project project = projectMapper.toEntity(request);

        project.setStatus(ProjectStatus.INITIATED);

        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public ProjectResponseDto getById(Long id) {

        return projectRepository.findById(id)
                .map(projectMapper::toDto)
                .orElseThrow(() -> new ProjectNotFoundException(
                        "Project not found with id!: " + id));
    }

    @Override
    public Page<ProjectResponseDto> getAll(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(projectMapper::toDto);
    }

    @Override
    public ProjectResponseDto update(Long id, ProjectUpdateRequestDto request) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(
                        "Project not found with id: " + id));

        project.setName(request.name());
        project.setDescription(request.description());
        project.setStartDate(request.startDate());
        project.setEndDate(request.endDate());

        Project savedProject = projectRepository.save(project);

        return projectMapper.toDto(savedProject);
    }

    @Override
    public void delete(Long id) {
        projectRepository.deleteById(id);
    }
}
