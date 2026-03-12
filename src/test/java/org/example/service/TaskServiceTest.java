package org.example.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.example.application.mapper.TaskMapper;
import org.example.application.service.impl.TaskServiceImpl;
import org.example.domain.entity.Project;
import org.example.domain.entity.Task;
import org.example.domain.entity.User;
import org.example.domain.entity.status.TaskPriority;
import org.example.domain.entity.status.TaskStatus;
import org.example.domain.exception.TaskNotFoundException;
import org.example.infrastructure.repository.LabelRepository;
import org.example.infrastructure.repository.ProjectRepository;
import org.example.infrastructure.repository.TaskRepository;
import org.example.infrastructure.repository.UserRepository;
import org.example.infrastructure.specification.impl.SpecificationBuilderImpl;
import org.example.presentation.dto.request.TaskCreateRequestDto;
import org.example.presentation.dto.request.TaskSearchParameterDto;
import org.example.presentation.dto.request.TaskUpdateRequestDto;
import org.example.presentation.dto.response.TaskResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LabelRepository labelRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private SpecificationBuilderImpl specificationBuilderImpl;
    @Mock
    private Specification<Task> specification;
    @InjectMocks
    private TaskServiceImpl taskServiceImpl;

    private User user;
    private Task task;

    private Task updatedTask;

    private TaskCreateRequestDto taskCreateRequest;

    private TaskResponseDto taskCreateResponse;

    private TaskUpdateRequestDto taskUpdateRequest;

    private TaskResponseDto taskUpdateResponse;

    private TaskSearchParameterDto taskSearchRequest;

    private Project project;

    private User createUser() {

        user = new User();
        user.setId(1L);

        return user;
    }

    private Project createProject() {

        project = new Project();
        project.setId(1L);
        project.setName("New project");

        return project;
    }

    private Task createTask() {

        task = new Task();
        task.setId(1L);
        task.setTitle("New task");
        task.setDescription("About new task");
        task.setProject(project);

        return task;
    }

    private Task updateTask() {

        updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated task");
        updatedTask.setDescription("About updated task");
        updatedTask.setProject(project);

        return updatedTask;
    }

    @BeforeEach
    void setUp() {

        user = createUser();
        project = createProject();
        task = createTask();
        updatedTask = updateTask();
    }

    private TaskCreateRequestDto createRequest() {

        return new TaskCreateRequestDto(
                "New task",
                "About new task",
                TaskPriority.HIGH,
                TaskStatus.NOT_STARTED,
                LocalDate.now(),
                1L,
                null,
                null);
    }

    private TaskUpdateRequestDto updateRequest() {

        return new TaskUpdateRequestDto(
                "Updated task",
                "About new updated task",
                TaskPriority.MEDIUM,
                TaskStatus.IN_PROGRESS,
                LocalDate.now(),
                1L,
                null);
    }

    private TaskSearchParameterDto searchParameterRequest() {

        return new TaskSearchParameterDto(
                new String[]{"New task"},
                new String[]{"NOT_STARTED"},
                new String[]{"HIGH"},
                new String[]{"1"},
                new String[]{"1"},
                new String[]{"2025-01-01"},
                new String[]{"2026-01-01"},
                new String[]{"2025-02-01"},
                new String[]{"2026-02-01"},
                new String[]{"false"});
    }

    public static TaskSearchParameterDto emptyParameterSearch() {

        return new TaskSearchParameterDto(null, null, null, null, null, null, null, null, null, null);
    }

    private TaskResponseDto createResponse() {

        return new TaskResponseDto(
                1L,
                "New task",
                "About new task",
                TaskPriority.HIGH,
                TaskStatus.NOT_STARTED,
                LocalDate.now(),
                LocalDateTime.now(),
                1L,
                1L,
                null);
    }

    private TaskResponseDto updateResponse() {

        return new TaskResponseDto(
                1L,
                "Updated task",
                "About new updated task",
                TaskPriority.MEDIUM,
                TaskStatus.IN_PROGRESS,
                LocalDate.now(),
                LocalDateTime.now(),
                1L,
                1L,
                null);
    }

    @Test
    @DisplayName("Create project")
    void createProject_success() {

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(taskMapper.toModel(createRequest())).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(createResponse());

        TaskResponseDto response = taskServiceImpl.create(createRequest());

        assertEquals(task.getTitle(), response.title());
        verify(taskRepository).save(task);
        verify(eventPublisher).publishEvent(any(TaskCreatedEvent.class));
    }

    @Test
    @DisplayName("Get task by specific id - exists")
    void getById_success() {

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(createResponse());

        TaskResponseDto response = taskServiceImpl.getById(task.getId());

        assertEquals(task.getTitle(), response.title());
    }

    @Test
    @DisplayName("Get task by specific id - not found")
    void getById_TaskNotFound() {

        when(taskRepository.findById(createTask().getId())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskServiceImpl.getById(createTask().getId()));
    }

    @Test
    @DisplayName("Delete task by specific id")
    void delete_success() {

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        taskServiceImpl.delete(task.getId());

        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("Find all existing task")
    void findAll_success() {

        when(taskRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(task)));
        when(taskMapper.toDto(task)).thenReturn(createResponse());

        Page<TaskResponseDto> page = taskServiceImpl.findAll(PageRequest.of(0, 10));

        assertEquals(1, page.getContent().size());
    }

    @Test
    @DisplayName("Update data for task")
    void update_success() {

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toDto(updatedTask)).thenReturn(updateResponse());

        TaskResponseDto result = taskServiceImpl.update(task.getId(), updateRequest());

        assertEquals(task.getTitle(), result.title());
        assertEquals(task.getDescription(), result.description());

        verify(taskRepository).findById(task.getId());
        verify(userRepository).findById(user.getId());
        verify(taskRepository).save(task);
        verify(taskMapper).toDto(updatedTask);
    }

    @Test
    @DisplayName("Update data for task - not found")
    void update_notFound() {

        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskServiceImpl.update(1L, updateRequest()));
    }

    @Test
    @DisplayName("Search parameters for task - return list")
    void search_success() {

        Pageable pageable = PageRequest.of(0, 10);

        Specification<Task> specification = specificationBuilderImpl.build(searchParameterRequest());

        when(taskRepository.findAll(specification, pageable)).thenReturn(new PageImpl<>(List.of(task)));
        when(taskMapper.toDto(task)).thenReturn(createResponse());

        Page<TaskResponseDto> result = taskServiceImpl.search(searchParameterRequest(), pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Search parameters for task - empty list")
    void search_emptyList() {

        Pageable pageable = PageRequest.of(0, 10);

        when(specificationBuilderImpl.build(emptyParameterSearch())).thenReturn(specification);
        when(taskRepository.findAll(specification, pageable)).thenReturn(Page.empty());

        Page<TaskResponseDto> result = taskServiceImpl.search(emptyParameterSearch(), pageable);

        assertTrue(result.isEmpty());
    }
}
