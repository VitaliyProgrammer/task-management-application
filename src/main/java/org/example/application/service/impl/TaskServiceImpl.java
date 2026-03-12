package org.example.application.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.application.mapper.TaskMapper;
import org.example.application.service.TaskService;
import org.example.domain.entity.Label;
import org.example.domain.entity.Project;
import org.example.domain.entity.Task;
import org.example.domain.entity.User;
import org.example.domain.entity.status.TaskPriority;
import org.example.domain.entity.status.TaskStatus;
import org.example.domain.event.SendEmailTaskEvent;
import org.example.domain.event.SendGoogleCalendarTaskEvent;
import org.example.domain.event.SendTelegramTaskEvent;
import org.example.domain.exception.LabelNotFoundException;
import org.example.domain.exception.ProjectNotFoundException;
import org.example.domain.exception.TaskNotFoundException;
import org.example.domain.exception.UserNotFoundException;
import org.example.infrastructure.repository.LabelRepository;
import org.example.infrastructure.repository.ProjectRepository;
import org.example.infrastructure.repository.TaskRepository;
import org.example.infrastructure.repository.UserRepository;
import org.example.infrastructure.security.CurrentUserProvider;
import org.example.infrastructure.specification.impl.SpecificationBuilderImpl;
import org.example.presentation.dto.request.EmailRequestDto;
import org.example.presentation.dto.request.GoogleCalendarRequestDto;
import org.example.presentation.dto.request.TaskCreateRequestDto;
import org.example.presentation.dto.request.TaskSearchParameterDto;
import org.example.presentation.dto.request.TaskUpdateRequestDto;
import org.example.presentation.dto.response.EmailResponseDto;
import org.example.presentation.dto.response.GoogleCalendarResponseDto;
import org.example.presentation.dto.response.TaskResponseDto;
import org.example.presentation.dto.response.TelegramResponseDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    private final LabelRepository labelRepository;

    private final SpecificationBuilderImpl specificationBuilderImpl;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final CurrentUserProvider currentUserProvider;

    @Override
    @Transactional
    public TaskResponseDto create(TaskCreateRequestDto request) {

        Task task = taskMapper.toModel(request);

        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project not found!"));

        task.setProject(project);

        if (request.assigneeId() != null) {
            User assignee = userRepository.findById(request.assigneeId())
                    .orElseThrow(() -> new UserNotFoundException("User not found!"));

            task.setAssignee(assignee);
        }

        if (request.labelIds() != null && !request.labelIds().isEmpty()) {

            Set<Label> labels = getLabelsByIds(request.labelIds());

            if (labels.size() != request.labelIds().size()) {
                throw new LabelNotFoundException("Some labels not found!");
            }

            task.setLabels(labels);
        }

        if (request.taskPriority() != null) {
            task.setTaskPriority(request.taskPriority());
        } else {
            task.setTaskPriority(TaskPriority.NO_SPECIFIED);
        }

        if (request.taskStatus() != null) {

            task.setTaskStatus(request.taskStatus());
        } else {
            task.setTaskStatus(TaskStatus.NO_SPECIFIED);
        }

        Task savedTask = taskRepository.save(task);

        return taskMapper.toDto(savedTask);
    }

    @Override
    public TaskResponseDto update(Long id, TaskUpdateRequestDto request) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found!"));

        if (request.name() != null) {
            task.setTitle(request.name());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.taskPriority() != null) {
            task.setTaskPriority(request.taskPriority());
        }
        if (request.taskStatus() != null) {
            task.setTaskStatus(request.taskStatus());
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }
        if (request.assigneeId() != null) {
            User assignee =
                    userRepository.findById(request.assigneeId())
                            .orElseThrow(() -> new UserNotFoundException("User not found!"));

            task.setAssignee(assignee);
        }

        if (request.labelIds() != null) {

            Set<Label> labels = getLabelsByIds(request.labelIds());

            if (labels.size() != request.labelIds().size()) {
                throw new LabelNotFoundException("Some labels not found!");
            }

            task.getLabels().clear();
            task.getLabels().addAll(labels);
        }

        Task savedTask = taskRepository.save(task);

        return taskMapper.toDto(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDto> findAll(Pageable pageable) {
        return taskRepository.findAll(pageable).map(taskMapper::toDto);
    }

    @Override
    public TaskResponseDto getById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(
                        "Task not found with id!: " + id));

        return taskMapper.toDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDto> search(TaskSearchParameterDto searchParameter, Pageable pageable) {

        Specification<Task> taskSpecification = specificationBuilderImpl.build(searchParameter);

        return taskRepository.findAll(taskSpecification, pageable).map(taskMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public GoogleCalendarResponseDto sendNotificationToGoogleCalendar(
            GoogleCalendarRequestDto request) {

        Task task = taskRepository.findById(request.taskId())
                .orElseThrow(() -> new TaskNotFoundException("Task no found!"));

        if (task.getProject() != null) {
            task.getProject().getName();
        }

        User senderUser = currentUserProvider.getAuthenticatedUser();

        if (senderUser.getGoogleRefreshToken() == null) {
            throw new IllegalStateException("User has not connected Google Calendar!");
        }

        applicationEventPublisher.publishEvent(new SendGoogleCalendarTaskEvent(
                task, senderUser, request.startDate(), request.endDate()));

        return new GoogleCalendarResponseDto(task.getId(), request.startDate(), request.endDate());
    }

    @Override
    @Transactional(readOnly = true)
    public EmailResponseDto sendNotificationToEmail(Long taskId, EmailRequestDto request) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task no found!"));

        if (task.getProject() != null) {
            task.getProject().getName();
        }

        User senderUser = currentUserProvider.getAuthenticatedUser();

        applicationEventPublisher.publishEvent(
                new SendEmailTaskEvent(task, senderUser, request.recipientEmail()));

        Instant shippingTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        return new EmailResponseDto(task.getId(), request.recipientEmail(), shippingTime);
    }

    @Override
    @Transactional(readOnly = true)
    public TelegramResponseDto sendNotificationToTelegram(Long taskId) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task no found!"));

        if (task.getProject() != null) {
            task.getProject().getName();
        }

        User assignee = task.getAssignee() != null ? task.getAssignee() : null;

        if (assignee == null || assignee.getTelegramChatId() == null) {
            throw new IllegalArgumentException("This assignee has no Telegram chatId!");
        }

        User senderUser = currentUserProvider.getAuthenticatedUser();

        applicationEventPublisher.publishEvent(new SendTelegramTaskEvent(task, senderUser));

        Instant shippingTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);

        return new TelegramResponseDto(task.getId(), assignee.getTelegramChatId(), shippingTime);
    }

    @Override
    public void delete(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found! " + id));

        taskRepository.delete(task);
    }

    private Set<Label> getLabelsByIds(Set<Long> labelIds) {

        Set<Label> labels = new HashSet<>();

        if (labelIds != null && !labelIds.isEmpty()) {
            labels.addAll(labelRepository.findAllById(labelIds));
        }

        return labels;
    }
}
