package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.LabelCreateRequestDto;
import org.example.dto.LabelResponseDto;
import org.example.entity.Label;
import org.example.exception.LabelAlreadyExistsException;
import org.example.exception.LabelNotFoundException;
import org.example.mapper.LabelMapper;
import org.example.repository.LabelRepository;
import org.example.service.LabelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public LabelResponseDto create(LabelCreateRequestDto request) {

        if (labelRepository.existsByName(request.name())) {
            throw new LabelAlreadyExistsException("Label with this name already exists!");
        }

        Label label = labelMapper.toEntity(request);
        Label savedLabel = labelRepository.save(label);

        return labelMapper.toDto(savedLabel);
    }

    @Override
    public LabelResponseDto update(Long id, LabelCreateRequestDto request) {

        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new LabelNotFoundException("Label not found!"));

        label.setName(request.name());
        label.setColor(request.color());
        label.setDescription(request.description());

        Label savedLabel = labelRepository.save(label);

        return labelMapper.toDto(savedLabel);
    }

    @Override
    public Page<LabelResponseDto> findAll(Pageable pageable) {
        return labelRepository.findAll(pageable)
                .map(labelMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
