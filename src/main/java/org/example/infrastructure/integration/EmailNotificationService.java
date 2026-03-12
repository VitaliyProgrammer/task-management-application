package org.example.infrastructure.integration;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.Task;
import org.example.domain.entity.User;
import org.example.domain.entity.status.TaskPriority;
import org.example.domain.entity.status.TaskStatus;
import org.example.domain.exception.UserNotFoundException;
import org.example.infrastructure.configuration.properties.EmailProperties;
import org.example.infrastructure.repository.UserRepository;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final EmailCredentialsCryptoService cryptoService;

    private final EmailProperties emailProperties;

    private final UserRepository userRepository;

    @Async("emailExecutor")
    public void sendTaskCreatedEmail(Task task, User senderUser, String recipientEmail) {

        log.warn("Email: start sending notification for task id={}", task.getId());

        if (!emailProperties.isEnabled()) {
            log.debug("Email: notifications are disabled!");
            return;
        }

        Optional<User> assignee = getAssigneeWithEmailCredentials(task);

        if (assignee.isEmpty()) {
            return;
        }

        String enteredRecipientEmail = resolveRecipient(recipientEmail);

        try {
            sendEmailWithRetry(task, senderUser, enteredRecipientEmail);

        } catch (MessagingException | UnsupportedEncodingException exception) {
            log.error("All retry attempts failed for task id={}", task.getId(), exception);
        }
    }

    @Retryable(
            retryFor = MessagingException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2))
    public void sendEmailWithRetry(Task task, User senderUser, String recipientEmail)
            throws MessagingException, UnsupportedEncodingException {

        if (!emailProperties.isEnabled()) {
            log.debug("Email notifications are disabled!");
            return;
        }

        if (senderUser.getEmailUsername() == null || senderUser.getEmailPassword() == null) {
            log.warn("Email not sent: missing credentials for sender id={}", senderUser.getId());
            return;
        }

        JavaMailSenderImpl javaMailSender = createSenderForUser(senderUser);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        TaskNotificationContent content = buildNotificationContent(task, senderUser);

        helper.setTo(recipientEmail);
        helper.setSubject("New task assigned: " + task.getTitle());
        helper.setText(buildEmailTextBody(content), true);
        helper.setFrom(new InternetAddress(
                senderUser.getEmailUsername(), "Task Management Application"));

        javaMailSender.send(message);
        log.info("Email sent to {} for task id={} from {}", recipientEmail, task.getId(),
                senderUser.getEmail());
    }

    private JavaMailSenderImpl createSenderForUser(User user) {

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(emailProperties.getHost());
        javaMailSender.setPort(emailProperties.getPort());
        javaMailSender.setUsername(user.getEmailUsername());

        String decryptedPassword = cryptoService.decrypt(user.getEmailPassword());
        javaMailSender.setPassword(decryptedPassword);

        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", emailProperties.isAuthentication());
        props.put("mail.smtp.starttls.enable", emailProperties.isStarttls());
        props.put("mail.smtp.connectiontimeout", 5000);
        props.put("mail.smtp.timeout", 5000);
        props.put("mail.smtp.writetimeout", 5000);
        props.put("mail.debug", true);

        return javaMailSender;
    }

    private Optional<User> getAssigneeWithEmailCredentials(Task task) {

        Long assigneeId = task.getAssignee() != null ? task.getAssignee().getId() : null;

        if (assigneeId == null) {
            log.warn("Email: task {} has no assignee", task.getId());
            return Optional.empty();
        }

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new UserNotFoundException("Assignee not found!"));

        if (assignee == null || assignee.getEmailUsername() == null
                || assignee.getEmailPassword() == null) {

            log.warn("Email: task {} has no assignee or missing email credentials ", task.getId());
            return Optional.empty();
        }

        return Optional.of(assignee);
    }

    private String resolveRecipient(String recipientEmail) {

        if (recipientEmail == null || recipientEmail.isBlank()) {
            throw new IllegalArgumentException("Recipient email can`t be null or empty!");
        }
        if (!recipientEmail.contains("@")) {
            throw new IllegalArgumentException("Invalid recipient email!");
        }

        return recipientEmail.trim();
    }

    private TaskNotificationContent buildNotificationContent(Task task, User senderUser) {

        return new TaskNotificationContent(
                task.getProject() != null
                        ? normalizeField(task.getProject().getName()) : "N/A",
                task.getTitle() != null ? normalizeField(task.getTitle()) : "N/A",
                task.getDescription() != null ? normalizeField(task.getDescription()) : "N/A",
                task.getDueDate() != null ? task.getDueDate() : LocalDate.now(),
                formatPriorityForEmail(task.getTaskPriority()),
                formatStatusForEmail(task.getTaskStatus()),
                senderUser.getEmail()
        );
    }

    private String buildEmailTextBody(TaskNotificationContent content) {

        return """
                📌 You have a new task assigned!<br><br>

                🏗️ <b>Project:</b> %s<br>
                📁 <b>Title:</b> %s<br>
                📝 <b>Description:</b> %s<br>
                📅 <b>Due date:</b> %s<br>
                🚨 <b>Priority:</b> %s<br>
                📊 <b>Status:</b> %s<br>
                👤 <b>Assigned by:</b> %s
                """.formatted(
                        content.projectName(),
                        content.title(),
                        content.description(),
                        content.dueDate(),
                        content.priority(),
                        content.status(),
                        content.assignedByEmail()
                );
    }

    private String normalizeField(String value) {

        if (value == null || value.isBlank() || "string".equals(value)) {
            return "N/A";
        }
        return value;
    }

    private String formatPriorityForEmail(TaskPriority priority) {

        if (priority == null || priority == TaskPriority.NO_SPECIFIED) {
            return "N/A";
        }

        return switch (priority) {
            case HIGH -> "<span style=\"color:red\">HIGH</span>";
            case MEDIUM -> "<span style=\"color:orange\">MEDIUM</span>";
            case LOW -> "<span style=\"color:green\">LOW</span>";
            default -> "N/A";
        };
    }

    private String formatStatusForEmail(TaskStatus status) {

        if (status == null || status == TaskStatus.NO_SPECIFIED) {
            return "N/A";
        }

        return switch (status) {
            case NOT_STARTED -> "<span style=\"color:red\">NOT_STARTED</span>";
            case IN_PROGRESS -> "<span style=\"color:orange\">IN_PROGRESS</span>";
            case COMPLETED -> "<span style=\"color:green\">COMPLETED</span>";
            default -> "N/A";
        };
    }
}
