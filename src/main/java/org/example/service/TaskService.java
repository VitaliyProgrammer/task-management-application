package org.example.service;

import org.example.dto.TaskCreateRequestDto;
import org.example.dto.TaskResponseDto;
import org.example.dto.TaskSearchParameterDto;
import org.example.dto.TaskUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskResponseDto create(TaskCreateRequestDto request);

    TaskResponseDto update(Long id, TaskUpdateRequestDto request);

    Page<TaskResponseDto> findAll(Pageable pageable);

    TaskResponseDto getById(Long id);

    Page<TaskResponseDto> search(TaskSearchParameterDto searchParameter, Pageable pageable);

    void delete(Long id);
}
