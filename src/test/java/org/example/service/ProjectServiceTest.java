package org.example.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.example.dto.ProjectCreateRequestDto;
import org.example.dto.ProjectResponseDto;
import org.example.dto.ProjectUpdateRequestDto;
import org.example.entity.Project;
import org.example.entity.status.ProjectStatus;
import org.example.exception.ProjectNotFoundException;
import org.example.mapper.ProjectMapper;
import org.example.repository.ProjectRepository;
import org.example.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectServiceImpl projectServiceImpl;

    private static final LocalDate FIXED_DATE = LocalDate.of(2025, 1, 31);

    private Project createProject() {

        Project project = new Project();
        project.setId(1L);
        project.setName("Test project");
        project.setDescription("About test project");
        project.setStartDate(LocalDate.now());
        project.setStatus(ProjectStatus.INITIATED);

        return project;
    }

    private ProjectCreateRequestDto requestProject() {

        return new ProjectCreateRequestDto(
                "Test project",
                "About test project",
                FIXED_DATE,
                null
        );
    }

    private ProjectResponseDto responseProject() {

        return new ProjectResponseDto(
                1L,
                "Test project",
                "About test project",
                FIXED_DATE,
                null,
                "INITIATED"
        );
    }

    private ProjectUpdateRequestDto updateRequest() {

        return new ProjectUpdateRequestDto(
                "Updated project",
                "About updated project",
                FIXED_DATE,
                null
        );
    }

    @Test
    @DisplayName("Create project - save project and return DTO")
    void create() {

        when(projectMapper.toEntity(requestProject()))
                .thenReturn(createProject());
        when(projectRepository.save(any())).thenReturn(createProject());
        when(projectMapper.toDto(any())).thenReturn(responseProject());

        ProjectResponseDto result = projectServiceImpl.create(requestProject());

        assertEquals("Test project", result.name());

        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("Get project by id - existing project")
    void getById() {

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(createProject()));
        when(projectMapper.toDto(any(Project.class))).thenReturn(responseProject());

        ProjectResponseDto result = projectServiceImpl.getById(1L);

        assertEquals(1L, result.id());
    }

    @Test
    @DisplayName("Find all projects with pagination")
    void getAll() {

        Page<Project> page = new PageImpl<>(List.of(createProject()));

        when(projectRepository.findAll(any(Pageable.class)))
                .thenReturn(page);
        when(projectMapper.toDto(any(Project.class))).thenReturn(responseProject());

        Page<ProjectResponseDto> result =
                projectServiceImpl.getAll(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Get project by id - project not found")
    void getById_notFound() {

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectServiceImpl.getById(1L));
    }

    @Test
    @DisplayName("Update project - update fields and return DTO")
    void update() {

        Project existingProject = createProject();

        when(projectRepository.findById(1L))
                .thenReturn(Optional.of(existingProject));
        when(projectRepository.save(any(Project.class))).thenReturn(existingProject);
        when(projectMapper.toDto(any(Project.class))).thenReturn(responseProject());

        ProjectResponseDto result = projectServiceImpl.update(1L, updateRequest());

        assertEquals("Updated project", existingProject.getName());
        assertEquals("About updated project", existingProject.getDescription());
        assertNotNull(result);
    }

    @Test
    @DisplayName("Delete project by id")
    void delete() {

        Project project = createProject();

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

        projectServiceImpl.delete(project.getId());

        verify(projectRepository).deleteById(project.getId());
    }
}
