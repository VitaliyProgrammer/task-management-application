package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.ProjectCreateRequestDto;
import org.example.dto.ProjectResponseDto;
import org.example.dto.ProjectUpdateRequestDto;
import org.example.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Endpoints for managing projects: create, search,"
        + "retrieve, update, and delete")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new project",
            description = "Creates a new project with the provided details")
    public ProjectResponseDto create(@RequestBody ProjectCreateRequestDto request) {
        return projectService.create(request);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Search project with filters and pagination",
            description = "Returns a paginated list of project based on search criteria")
    public ProjectResponseDto getById(@PathVariable Long id) {
        return projectService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Return list of all available projects",
            description = "Return all available projects that admin created")
    public Page<ProjectResponseDto> getAll(Pageable pageable) {
        return projectService.getAll(pageable);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update project", description = "Update an existing project")
    public ProjectResponseDto update(
            @PathVariable Long id, @RequestBody @Valid ProjectUpdateRequestDto request) {

        return projectService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete project", description = "Deletes a project by its ID")
    public void delete(@PathVariable Long id) {
        projectService.delete(id);
    }
}
