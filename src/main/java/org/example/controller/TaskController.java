package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.TaskCreateRequestDto;
import org.example.dto.TaskResponseDto;
import org.example.dto.TaskSearchParameterDto;
import org.example.dto.TaskUpdateRequestDto;
import org.example.service.TaskService;
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
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks",
        description = "Operation for managing tasks: create, update, deletion, retrieval, "
                + "search with filtering and pagination")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Create a new task", description = "Create a new task within a project")
    public TaskResponseDto create(@RequestBody @Valid TaskCreateRequestDto request) {
        return taskService.create(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Search and filter tasks",
            description = "Retrieve task using dynamic search and criteria: "
                    + "title, status, priority, assignee, project, creation date, due date and "
                    + "attachment presence. API supports pagination.")
    public Page<TaskResponseDto> search(TaskSearchParameterDto searchParameter, Pageable pageable) {
        return taskService.search(searchParameter, pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Get specific task by ID",
            description = "Retrieve detailed information about specific task")
    public TaskResponseDto getTaskById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Update task",
            description = "Update task`s details such as title, description, status, priority, "
                    + "due date, assignee, or labels")
    public TaskResponseDto update(@PathVariable Long id,
                                  @RequestBody @Valid TaskUpdateRequestDto request) {

        return taskService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete task", description = "Delete a task y its ID")
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
