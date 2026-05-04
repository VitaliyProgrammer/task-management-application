package org.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.example.domain.entity.Project;
import org.example.domain.entity.Task;
import org.example.domain.entity.status.ProjectStatus;
import org.example.domain.entity.status.TaskPriority;
import org.example.domain.entity.status.TaskStatus;
import org.example.infrastructure.configuration.listener.EmailNotificationTaskListener;
import org.example.infrastructure.integration.EmailNotificationService;
import org.example.infrastructure.repository.ProjectRepository;
import org.example.infrastructure.repository.TaskRepository;
import org.example.infrastructure.security.jwt.JwtAuthenticationFilter;
import org.example.infrastructure.security.jwt.JwtUtil;
import org.example.presentation.dto.request.TaskCreateRequestDto;
import org.example.presentation.dto.request.TaskSearchParameterDto;
import org.example.presentation.dto.request.TaskUpdateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

   /* @MockBean
    private EmailNotificationTaskListener emailNotificationTaskListener;

    @MockBean
    private EmailNotificationService emailNotificationService;*/

   /* @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;*/

    @MockBean
    private JwtUtil jwtUtil;

    private Project savedProject;

    private Task savedTask;

    private static final LocalDate FIXED_DATE = LocalDate.of(2025, 1, 1);

    @BeforeEach
    void setUp() {

        taskRepository.deleteAll();
        projectRepository.deleteAll();

        savedProject = createProject();
        savedTask = createTask(savedProject);
    }

    private Project createProject() {

        Project project = new Project();

        project.setName("New project");
        project.setDescription("About new project");
        project.setStartDate(FIXED_DATE);
        project.setEndDate(FIXED_DATE.plusDays(7));
        project.setStatus(ProjectStatus.INITIATED);

        return projectRepository.save(project);
    }

    private Task createTask(Project project) {

        Task task = new Task();

        task.setTitle("New task");
        task.setDescription("About new task");
        task.setTaskPriority(TaskPriority.HIGH);
        task.setTaskStatus(TaskStatus.NOT_STARTED);
        task.setDueDate(FIXED_DATE);
        task.setProject(project);

        return taskRepository.save(task);
    }

    private TaskCreateRequestDto createRequest() {

        return new TaskCreateRequestDto(
                "New task",
                "About new task",
                TaskPriority.HIGH,
                TaskStatus.NOT_STARTED,
                FIXED_DATE,
                savedProject.getId(),
                null,
                null);
    }

    private TaskUpdateRequestDto updateRequest() {

        return new TaskUpdateRequestDto(
                "Updated task",
                "About new updated task",
                TaskPriority.MEDIUM,
                TaskStatus.IN_PROGRESS,
                FIXED_DATE,
                null,
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

    @Test
    @DisplayName("POST /task - create new task")
    @WithMockUser(
            username = "test",
            roles = {"ADMIN", "USER"})
    void create() throws Exception {

        String response = mockMvc.perform(post("/tasks")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(createRequest())))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);

        assertNotNull(jsonNode.get("id"));
        assertEquals(createRequest().title(), jsonNode.get("title").asText());
        assertEquals(createRequest().description(), jsonNode.get("description").asText());
    }

    @Test
    @DisplayName("POST /tasks/{id} -get specific task")
    @WithMockUser(username = "test", roles = {"ADMIN", "USER"})
    void getById() throws Exception {

        String response = mockMvc.perform(get("/tasks/{id}", savedTask.getId()))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);

        assertEquals(savedTask.getId(), jsonNode.get("id").asLong());
        assertEquals(savedTask.getTitle(), jsonNode.get("title").asText());
        assertEquals(savedTask.getDescription(), jsonNode.get("description").asText());
    }

    @Test
    @DisplayName("PUT /tasks/{id} - update task")
    @WithMockUser(
            username = "test",
            roles = {"ADMIN", "USER"})
    void update() throws Exception {

        assertTrue(taskRepository.findById(savedTask.getId()).isPresent());

        String response = mockMvc.perform(put("/tasks/{id}", savedTask.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(updateRequest())))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);

        assertEquals(updateRequest().name(), jsonNode.get("title").asText());
        assertEquals(updateRequest().description(), jsonNode.get("description").asText());
    }

    @Test
    @DisplayName("GET /tasks - search parameters of task")
    @WithMockUser(
            username = "test",
            roles = {"ADMIN", "USER"})
    void search() throws Exception {

        String response = mockMvc.perform(get("/tasks")
                                        .param("title", searchParameterRequest().title())
                                        .param("page", "0")
                                        .param("size", "10"))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode firstTask = objectMapper.readTree(response).get("content").get(0);

        assertEquals(searchParameterRequest().title()[0], firstTask.get("title").asText());
    }

    @Test
    @DisplayName("DELETE /tasks/{id} - delete task")
    @WithMockUser(
            username = "test",
            roles = {"ADMIN"})
    void delete_success() throws Exception {

        mockMvc.perform(delete("/tasks/{id}", savedTask.getId())).andExpect(status().isNoContent());

        assertTrue(taskRepository.findById(savedTask.getId()).isEmpty());
    }

    @Test
    @DisplayName("DELETE /tasks/{id} - task not found")
    @WithMockUser(
            username = "test",
            roles = {"ADMIN"})
    void delete_notFound() throws Exception {

        mockMvc.perform(delete("/tasks/{id}", 99999L))
                .andExpect(status().isNotFound());
    }
}
