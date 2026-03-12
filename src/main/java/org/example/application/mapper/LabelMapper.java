package org.example.application.mapper;

import org.example.domain.entity.Label;
import org.example.presentation.dto.request.LabelCreateRequestDto;
import org.example.presentation.dto.response.LabelResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    @Mapping(target = "id", ignore = true)
    Label toEntity(LabelCreateRequestDto request);

    LabelResponseDto toDto(Label label);
}
