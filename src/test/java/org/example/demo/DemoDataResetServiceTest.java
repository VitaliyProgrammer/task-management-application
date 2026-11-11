package org.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.example.application.service.DropBoxService;
import org.example.infrastructure.demo.DemoDataResetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ActiveProfiles("test")
@Import(DemoDataResetService.class)
@TestPropertySource(properties = "app.demo.reset.enabled=true")
class DemoDataResetServiceTest {

    private static final String DEMO_EMAIL = "demo@alijenta.dev";

    @Autowired
    private DemoDataResetService demoDataResetService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private DropBoxService dropBoxService;

    @Test
    @DisplayName("Reset removes visitor data and loads the demo workspace")
    void resetDemoData_replacesVisitorData() {

        insertVisitorTaskWithAttachment("id:visitor-file");
        when(dropBoxService.deleteFile(anyString())).thenReturn(true);

        demoDataResetService.resetDemoData();

        assertEquals(0, count("SELECT COUNT(*) FROM users WHERE email = 'visitor@example.com'"));
        assertEquals(1, count("SELECT COUNT(*) FROM users WHERE email = '" + DEMO_EMAIL + "'"));
        assertEquals(4, count("SELECT COUNT(*) FROM users"));
        assertEquals(5, count("SELECT COUNT(*) FROM projects"));
        assertEquals(7, count("SELECT COUNT(*) FROM tasks"));
        assertEquals(0, count("SELECT COUNT(*) FROM attachments"));
        verify(dropBoxService).deleteFile("id:visitor-file");
    }

    @Test
    @DisplayName("Running the reset twice gives the same demo workspace")
    void resetDemoData_isRepeatable() {

        demoDataResetService.resetDemoData();
        demoDataResetService.resetDemoData();

        assertEquals(4, count("SELECT COUNT(*) FROM users"));
        assertEquals(5, count("SELECT COUNT(*) FROM projects"));
        assertEquals(5, count("SELECT COUNT(*) FROM labels"));
        assertEquals(1, count("SELECT COUNT(*) FROM users_roles WHERE role_id = 1"));
        verify(dropBoxService, never()).deleteFile(anyString());
    }

    private void insertVisitorTaskWithAttachment(String dropBoxFileId) {

        jdbcTemplate.update("INSERT INTO users (email, password, first_name, last_name) "
                + "VALUES ('visitor@example.com', 'hash', 'Visitor', 'User')");
        jdbcTemplate.update("INSERT INTO projects (name, status) VALUES ('Visitor project', "
                + "'INITIATED')");
        jdbcTemplate.update("INSERT INTO tasks (title, task_priority, task_status, project_id) "
                + "SELECT 'Visitor task', 'LOW', 'NOT_STARTED', id FROM projects "
                + "WHERE name = 'Visitor project'");
        jdbcTemplate.update("INSERT INTO attachments (drop_box_field_id, filename, upload_date, "
                + "task_id) SELECT ?, 'cv.pdf', NOW(), id FROM tasks WHERE title = 'Visitor task'",
                dropBoxFileId);
    }

    private int count(String sql) {

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
