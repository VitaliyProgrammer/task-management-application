package org.example.service.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.example.dto.AttachmentResponseDto;
import org.example.entity.Attachment;
import org.example.entity.Task;
import org.example.exception.TaskNotFoundException;
import org.example.mapper.AttachmentMapper;
import org.example.repository.AttachmentRepository;
import org.example.repository.TaskRepository;
import org.example.service.AttachmentService;
import org.example.service.DropBoxService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final DropBoxService dropBoxService;
    private final AttachmentMapper attachmentMapper;

    @Override
    public AttachmentResponseDto uploadFile(Long taskId, MultipartFile file) {

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(
                        "Task not found with id: " + taskId
                ));

        String dropBoxField = dropBoxService.uploadFile(file);

        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setDropBoxFieldId(dropBoxField);
        attachment.setFilename(file.getOriginalFilename());
        attachment.setUploadDate(LocalDateTime.now());

        Attachment savedAttachment = attachmentRepository.save(attachment);

        return attachmentMapper.toDto(savedAttachment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttachmentResponseDto> findAttachmentsByTaskId(Long taskId, Pageable pageable) {

        return attachmentRepository.findAllAttachmentsByTaskId(taskId, pageable)
                .map(attachmentMapper::toDto);
    }
}
