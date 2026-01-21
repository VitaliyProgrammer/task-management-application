package org.example.service.integration;

import java.util.List;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.configuration.properties.EmailProperties;
import org.example.entity.Task;
import org.example.entity.User;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {
    private final EmailProperties emailProperties;

    private final EmailCredentialsCryptoService cryptoService;

    @Async
    public void sendTaskCreatedEmail(Task task) {

        if (!emailProperties.isEnabled()) {
            log.debug("Email notifications are disabled!");
            return;
        }

        User assignee = task.getAssignee();

        if (assignee == null || assignee.getEmail() == null) {
            log.warn("Email not sent: no assignee or email for task id={} ", task.getId());
            return;
        }

        if (assignee.getEmailUsername() == null || assignee.getEmailPassword() == null) {
            log.warn("Email not sent: SMTP credentials not set for user id={}", assignee.getId());
        }

        try {
            final JavaMailSender javaMailSender = createSenderForUser(assignee);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(assignee.getEmail());
            message.setSubject("New task assigned: " + task.getTitle());
            message.setText(buildEmailTextBody(task));

            javaMailSender.send(message);

            log.info("Email sent to {} for task id={}", assignee.getEmail(), task.getId());
        } catch (MailException exception) {
            log.error("Failed to send email to {} for task id={}",
                    assignee.getEmail(), task.getId(), exception);
        }
    }

    private JavaMailSender createSenderForUser(User user) {

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

        javaMailSender.setHost(emailProperties.getHost());
        javaMailSender.setPort(emailProperties.getPort());
        javaMailSender.setUsername(user.getEmailUsername());

        String decryptedPassword = cryptoService.decrypt(user.getEmailPassword());
        javaMailSender.setPassword(decryptedPassword);

        Properties properties = javaMailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", emailProperties.isAuthentication());
        properties.put("mail.smtp.starttls.enable", emailProperties.isStarttls());

        return javaMailSender;
    }

    private String buildEmailTextBody(Task task) {

        List<String> textBody = List.of(
                "!Hi!",
                "",
                "New task assigned!",
                "",
                "Title: " + task.getTitle(),
                "Description: " + task.getDescription(),
                "Due date: " + task.getDueDate(),
                "Priority: " + task.getTaskPriority()
        );

        StringBuilder body = new StringBuilder();

        for (String line : textBody) {
            body.append(line).append("\n");
        }

        return body.toString();
    }
}
