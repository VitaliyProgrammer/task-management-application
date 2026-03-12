package org.example.application.service;

import org.example.presentation.dto.request.LabelCreateRequestDto;
import org.example.presentation.dto.response.LabelResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {

    LabelResponseDto create(LabelCreateRequestDto request);

    LabelResponseDto update(Long id, LabelCreateRequestDto request);

    Page<LabelResponseDto> findAll(Pageable pageable);

    void delete(Long id);
}
