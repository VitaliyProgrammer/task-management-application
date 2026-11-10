package org.example.application.mapper;

import java.util.Set;
import java.util.stream.Collectors;
import org.example.domain.entity.Role;
import org.example.domain.entity.User;
import org.example.presentation.dto.request.UserPutProfileRequestDto;
import org.example.presentation.dto.request.UserRegistrationRequestDto;
import org.example.presentation.dto.response.UserProfileResponseDto;
import org.example.presentation.dto.response.UserRegistrationResponseDto;
import org.example.presentation.dto.response.UserSummaryResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toModel(UserRegistrationRequestDto dto);

    UserRegistrationResponseDto toDto(User user);

    @Mapping(target = "emailConnected", expression = "java(hasEmailCredentials(user))")
    @Mapping(target = "googleCalendarConnected",
            expression = "java(user.getGoogleRefreshToken() != null)")
    UserProfileResponseDto toProfileDto(User user);

    @Mapping(target = "telegramConnected", expression = "java(user.getTelegramChatId() != null)")
    @Mapping(target = "emailConnected", expression = "java(hasEmailCredentials(user))")
    UserSummaryResponseDto toSummaryDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateUserFromDto(UserPutProfileRequestDto dto, @MappingTarget User user);

    default Set<String> mapRoleToString(Set<Role> roles) {

        return roles.stream()
                .map(role -> role.getRoleName().name())
                .collect(Collectors.toSet());
    }

    default boolean hasEmailCredentials(User user) {

        return user.getEmailUsername() != null && user.getEmailPassword() != null;
    }
}
