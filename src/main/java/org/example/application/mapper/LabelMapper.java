package org.example.application.mapper;

import org.example.domain.entity.Label;
import org.example.presentation.dto.request.LabelRequestDto;
import org.example.presentation.dto.response.LabelResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    @Mapping(target = "id", ignore = true)
    Label toEntity(LabelRequestDto request);

    LabelResponseDto toDto(Label label);

    void updateLabelFromDto(LabelRequestDto dto, @MappingTarget Label label);
}
