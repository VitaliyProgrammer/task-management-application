package org.example.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.example.domain.entity.Project;
import org.example.domain.entity.status.ProjectStatus;
import org.example.infrastructure.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    private Project createProject() {

        Project project = new Project();
        project.setName("Test project");
        project.setDescription("About test project");
        project.setStartDate(LocalDate.now());
        project.setStatus(ProjectStatus.INITIATED);

        return project;
    }

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
    }

    @Test
    @DisplayName("Create new project")
    void saveProject() {

        Project project = projectRepository.save(createProject());

        assertNotNull(project.getId());
    }

    @Test
    @DisplayName("Find project by id")
    void findById() {

        Project project = projectRepository.save(createProject());

        Optional<Project> foundProject = projectRepository.findById(project.getId());

        assertTrue(foundProject.isPresent());
        assertEquals("Test project", foundProject.get().getName());
    }

    @Test
    @DisplayName("Find all projects")
    void findAll() {

        projectRepository.save(createProject());
        projectRepository.save(createProject());

        Page<Project> page = projectRepository.findAll(PageRequest.of(0, 10));

        assertEquals(2, page.getTotalElements());
    }

    @Test
    @DisplayName("Delete project by id")
    void deleteById() {

        Project project = projectRepository.save(createProject());

        projectRepository.deleteById(project.getId());

        Optional<Project> deletedProject = projectRepository.findById(project.getId());

        assertTrue(deletedProject.isEmpty());
    }
}
