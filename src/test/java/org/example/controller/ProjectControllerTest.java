package org.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.example.domain.entity.Project;
import org.example.domain.entity.status.ProjectStatus;
import org.example.infrastructure.repository.ProjectRepository;
import org.example.presentation.dto.request.ProjectCreateRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    private static final LocalDate FIXED_DATE = LocalDate.of(2025, 1, 31);

    private Project createProject() {

        Project project = new Project();
        project.setName("Test project");
        project.setDescription("Test description");
        project.setStatus(ProjectStatus.INITIATED);

        return projectRepository.save(project);
    }

    private ProjectCreateRequestDto requestProject() {

        return new ProjectCreateRequestDto("New project", "About new project", FIXED_DATE, null);
    }

    @Test
    @DisplayName("POST /projects - create new project")
    @WithMockUser(username = "test", roles = "ADMIN")
    void create() throws Exception {

        String response =
                mockMvc
                        .perform(
                                post("/projects")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(requestProject())))
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);

        assertNotNull(jsonNode.get("id"));
        assertEquals("New project", jsonNode.get("name").asText());
        assertEquals("About new project", jsonNode.get("description").asText());
    }

    @Test
    @DisplayName("GET /projects/{id} - return project by id")
    @WithMockUser(
            username = "test",
            roles = {"ADMIN", "USER"})
    void getProjectById() throws Exception {

        Project existingProject = createProject();

        String response =
                mockMvc
                        .perform(get("/projects/{id}", existingProject.getId()))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);

        assertEquals(existingProject.getId().longValue(), jsonNode.get("id").asLong());
        assertEquals(existingProject.getName(), jsonNode.get("name").asText());
        assertEquals(existingProject.getDescription(), jsonNode.get("description").asText());
    }

    @Test
    @DisplayName("GET /projects/{id} - project by id not existing")
    @WithMockUser(
            username = "test",
            roles = {"ADMIN", "USER"})
    void getProjectById_notFound() throws Exception {

        mockMvc.perform(get("/projects/{id}", 99999L)).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /projects - return all projects")
    @WithMockUser(
            username = "test",
            roles = {"ADMIN", "USER"})
    void getAllProjects() throws Exception {

        createProject();
        createProject();

        String response =
                mockMvc
                        .perform(
                                get("/projects")
                                        .param("page", "0")
                                        .param("size", "10")
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode content = jsonNode.get("content");

        assertThat(content).isNotNull();
        assertThat(content.size()).isGreaterThanOrEqualTo(2);

        JsonNode firstProject = content.get(0);
        assertThat(firstProject.get("name").asText()).isNotBlank();
        assertThat(firstProject.get("description").asText()).isNotBlank();
        assertThat(firstProject.get("status").asText()).isEqualTo(createProject().getStatus().name());
    }

    @Test
    @DisplayName("GET /projects - return empty list")
    @WithMockUser(
            username = "test",
            roles = {"ADMIN", "USER"})
    void getAllProjects_emptyList() throws Exception {

        projectRepository.deleteAll();

        String response =
                mockMvc
                        .perform(
                                get("/projects")
                                        .param("page", "0")
                                        .param("size", "10")
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode content = jsonNode.get("content");

        assertThat(content.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("DELETE /projects/{id} - delete project by id")
    @WithMockUser(username = "test", roles = "ADMIN")
    void deleteProject() throws Exception {

        Project existingProject = createProject();

        mockMvc
                .perform(delete("/projects/{id}", existingProject.getId()))
                .andExpect(status().isNoContent());

        assertTrue(projectRepository.findById(existingProject.getId()).isEmpty());
    }

    @Test
    @DisplayName("DELETE /projects/{id} - project not found")
    @WithMockUser(username = "test", roles = "ADMIN")
    void deleteProject_notFound() throws Exception {

        mockMvc.perform(delete("/projects/{id}", 99999L)).andExpect(status().isNotFound());
    }
}
