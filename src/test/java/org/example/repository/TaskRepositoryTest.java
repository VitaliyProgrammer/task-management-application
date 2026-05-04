package org.example.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.example.domain.entity.Task;
import org.example.domain.entity.status.TaskPriority;
import org.example.domain.entity.status.TaskStatus;
import org.example.infrastructure.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private Task task;

    private Task createTask() {

        task = new Task();
        task.setTitle("New task");
        task.setDescription("About new task");
        task.setTaskStatus(TaskStatus.NOT_STARTED);
        task.setTaskPriority(TaskPriority.HIGH);
        task.setDueDate(LocalDate.now());

        return task;
    }

    @BeforeEach
    void setUp() {

        task = taskRepository.save(createTask());
    }

    @Test
    @DisplayName("Save new task")
    void saveTask_success() {

        Task savedTask = taskRepository.save(createTask());

        assertNotNull(savedTask.getId());
        assertEquals(createTask().getTitle(), savedTask.getTitle());
    }

    @Test
    @DisplayName("Find task by id - success")
    void findById_success() {

        Optional<Task> foundTask = taskRepository.findById(task.getId());

        assertTrue(foundTask.isPresent());
        assertEquals(createTask().getTitle(), foundTask.get().getTitle());
    }

    @Test
    @DisplayName("Find all tasks - success")
    void findAll_success() {

        List<Task> foundTasks = taskRepository.findAll();

        assertFalse(foundTasks.isEmpty());
    }

    @Test
    @DisplayName("Delete task by id - success")
    void deleteById_success() {

        taskRepository.deleteById(task.getId());

        assertTrue(taskRepository.findById(task.getId()).isEmpty());
    }
}
