package org.example.application.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.application.mapper.LabelMapper;
import org.example.application.service.LabelService;
import org.example.domain.entity.Label;
import org.example.domain.exception.LabelAlreadyExistsException;
import org.example.domain.exception.LabelNotFoundException;
import org.example.infrastructure.repository.LabelRepository;
import org.example.presentation.dto.request.LabelRequestDto;
import org.example.presentation.dto.response.LabelResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public LabelResponseDto create(LabelRequestDto request) {

        if (labelRepository.existsByName(request.name())) {
            throw new LabelAlreadyExistsException("Label with this name already exists!");
        }

        Label label = labelMapper.toEntity(request);
        Label savedLabel = labelRepository.save(label);

        return labelMapper.toDto(savedLabel);
    }

    @Override
    @Transactional
    public LabelResponseDto update(Long id, LabelRequestDto request) {

        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new LabelNotFoundException("Label not found!"));

        labelMapper.updateLabelFromDto(request, label);

        return labelMapper.toDto(label);
    }

    @Override
    public Page<LabelResponseDto> findAll(Pageable pageable) {
        return labelRepository.findAll(pageable).map(labelMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
