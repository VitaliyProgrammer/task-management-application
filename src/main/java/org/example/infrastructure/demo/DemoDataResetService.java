package org.example.infrastructure.demo;

import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.application.service.DropBoxService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Keeps the public demo usable: once a day every visitor change is wiped and the demo
 * workspace from {@code db/demo/demo-data.sql} is loaded again, so the next visitor never
 * sees an empty or broken app (for example after someone deleted all projects or changed
 * the demo password).
 *
 * <p>It deletes ALL data except roles, so it is enabled only on the demo deployment
 * ({@code app.demo.reset.enabled=true}).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.demo.reset", name = "enabled", havingValue = "true")
public class DemoDataResetService {

    /** Child tables first, so foreign keys never block a delete. Roles are kept. */
    private static final List<String> TABLES_IN_DELETE_ORDER = List.of(
            "tasks_labels",
            "comments",
            "attachments",
            "tasks",
            "projects",
            "labels",
            "google_oauth_state",
            "users_roles",
            "users");

    private static final Resource DEMO_DATA = new ClassPathResource("db/demo/demo-data.sql");

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final TransactionTemplate transactionTemplate;
    private final DropBoxService dropBoxService;

    @Scheduled(cron = "${app.demo.reset.cron:0 0 3 * * *}", zone = "UTC")
    public void resetDemoData() {

        List<String> uploadedFiles = transactionTemplate.execute(status -> replaceData());

        // Files are removed only after the database commit: if the reset fails, the
        // attachments still point to existing files.
        long deleted = uploadedFiles.stream().filter(dropBoxService::deleteFile).count();

        log.info("Demo data reset: database reloaded, {} of {} Dropbox files removed",
                deleted, uploadedFiles.size());
    }

    private List<String> replaceData() {

        List<String> uploadedFiles = jdbcTemplate.queryForList(
                "SELECT drop_box_field_id FROM attachments", String.class);

        TABLES_IN_DELETE_ORDER.forEach(table -> jdbcTemplate.update("DELETE FROM " + table));

        // The connection bound to the current transaction, so the script and the deletes
        // commit or roll back together.
        Connection connection = DataSourceUtils.getConnection(dataSource);
        new ResourceDatabasePopulator(DEMO_DATA).populate(connection);

        return uploadedFiles;
    }
}
