package org.example.service;

import org.example.dto.LabelCreateRequestDto;
import org.example.dto.LabelResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {

    LabelResponseDto create(LabelCreateRequestDto request);

    LabelResponseDto update(Long id, LabelCreateRequestDto request);

    Page<LabelResponseDto> findAll(Pageable pageable);

    void delete(Long id);
}
