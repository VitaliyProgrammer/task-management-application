package org.example.mapper;

import org.example.dto.LabelCreateRequestDto;
import org.example.dto.LabelResponseDto;
import org.example.entity.Label;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    @Mapping(target = "id", ignore = true)
    Label toEntity(LabelCreateRequestDto request);

    LabelResponseDto toDto(Label label);
}
