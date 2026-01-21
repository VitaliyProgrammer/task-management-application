package org.example.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.example.dto.UserProfileResponseDto;
import org.example.dto.UserRegistrationRequestDto;
import org.example.dto.UserRegistrationResponseDto;
import org.example.entity.Role;
import org.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toModel(UserRegistrationRequestDto dto);

    UserRegistrationResponseDto toDto(User user);

    UserProfileResponseDto toProfileDto(User user);

    default Set<String> mapRoleToString(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());
    }
}
