package org.example.service.impl;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.dto.TaskCreateRequestDto;
import org.example.dto.TaskResponseDto;
import org.example.dto.TaskSearchParameterDto;
import org.example.dto.TaskUpdateRequestDto;
import org.example.entity.Label;
import org.example.entity.Project;
import org.example.entity.Task;
import org.example.entity.User;
import org.example.event.TaskCreatedEvent;
import org.example.exception.LabelNotFoundException;
import org.example.exception.ProjectNotFoundException;
import org.example.exception.TaskNotFoundException;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TaskMapper;
import org.example.repository.LabelRepository;
import org.example.repository.ProjectRepository;
import org.example.repository.TaskRepository;
import org.example.repository.UserRepository;
import org.example.repository.impl.SpecificationBuilderImpl;
import org.example.service.TaskService;
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

    private final ApplicationEventPublisher eventPublisher;

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

        Task savedTask = taskRepository.save(task);

        eventPublisher.publishEvent(new TaskCreatedEvent(savedTask));

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
            User assignee = userRepository.findById(request.assigneeId())
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
        return taskRepository.findAll(pageable)
                .map(taskMapper::toDto);
    }

    @Override
    public TaskResponseDto getById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id!: " + id));

        return taskMapper.toDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskResponseDto> search(TaskSearchParameterDto searchParameter,
                                        Pageable pageable) {

        Specification<Task> taskSpecification = specificationBuilderImpl.build(searchParameter);

        return taskRepository.findAll(taskSpecification, pageable)
                .map(taskMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    private Set<Label> getLabelsByIds(Set<Long> labelIds) {

        Set<Label> labels = new HashSet<>();

        if (labelIds != null && !labelIds.isEmpty()) {
            labels.addAll(labelRepository.findAllById(labelIds));
        }

        return labels;
    }
}
