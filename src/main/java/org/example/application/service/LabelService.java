package org.example.application.service;

import org.example.presentation.dto.request.LabelRequestDto;
import org.example.presentation.dto.response.LabelResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {

    LabelResponseDto create(LabelRequestDto request);

    LabelResponseDto update(Long id, LabelRequestDto request);

    Page<LabelResponseDto> findAll(Pageable pageable);

    void delete(Long id);
}
