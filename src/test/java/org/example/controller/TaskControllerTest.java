package org.example.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.example.dto.TaskCreateRequestDto;
import org.example.dto.TaskResponseDto;
import org.example.dto.TaskSearchParameterDto;
import org.example.dto.TaskUpdateRequestDto;
import org.example.entity.Project;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.entity.status.TaskPriority;
import org.example.entity.status.TaskStatus;
import org.example.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskRepositoryTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskServiceImpl taskServiceImpl;

    private User user;
    private Task task;

    private Task updatedTask;

    private TaskCreateRequestDto taskCreateRequest;

    private TaskResponseDto taskCreateResponse;

    private TaskUpdateRequestDto taskUpdateRequest;

    private TaskResponseDto taskUpdateResponse;

    private TaskSearchParameterDto taskSearchRequest;

    private TaskSearchParameterDto taskSearchEmptyListRequest;

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
        taskCreateRequest = createRequest();
        taskCreateResponse = createResponse();
        taskUpdateRequest = updateRequest();
        taskUpdateResponse = updateResponse();
        taskSearchRequest = searchParameterRequest();
        taskSearchEmptyListRequest = emptyParameterSearch();
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
                null
        );
    }

    private TaskUpdateRequestDto updateRequest() {

        return new TaskUpdateRequestDto(
                "Updated task",
                "About new updated task",
                TaskPriority.MEDIUM,
                TaskStatus.IN_PROGRESS,
                LocalDate.now(),
                1L,
                null
        );
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
                new String[]{"false"}
        );
    }

    public static TaskSearchParameterDto emptyParameterSearch() {

        return new TaskSearchParameterDto(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
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
                null
        );
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
                null
        );
    }


}
