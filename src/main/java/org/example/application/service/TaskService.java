package org.example.application.service;

import org.example.presentation.dto.request.EmailRequestDto;
import org.example.presentation.dto.request.GoogleCalendarRequestDto;
import org.example.presentation.dto.request.TaskCreateRequestDto;
import org.example.presentation.dto.request.TaskSearchParameterDto;
import org.example.presentation.dto.request.TaskUpdateRequestDto;
import org.example.presentation.dto.request.TelegramRequestDto;
import org.example.presentation.dto.response.EmailResponseDto;
import org.example.presentation.dto.response.GoogleCalendarResponseDto;
import org.example.presentation.dto.response.TaskResponseDto;
import org.example.presentation.dto.response.TelegramResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {

    TaskResponseDto create(TaskCreateRequestDto request);

    TaskResponseDto update(Long id, TaskUpdateRequestDto request);

    Page<TaskResponseDto> findAll(Pageable pageable);

    TaskResponseDto getById(Long id);

    Page<TaskResponseDto> search(TaskSearchParameterDto searchParameter, Pageable pageable);

    GoogleCalendarResponseDto sendNotificationToGoogleCalendar(GoogleCalendarRequestDto request);

    EmailResponseDto sendNotificationToEmail(Long taskId, EmailRequestDto request);

    TelegramResponseDto sendNotificationToTelegram(Long taskId);

    void delete(Long id);
}
