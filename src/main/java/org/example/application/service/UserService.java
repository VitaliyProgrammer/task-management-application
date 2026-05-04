package org.example.application.service;

import java.util.List;
import java.util.Optional;
import org.example.domain.entity.User;
import org.example.presentation.dto.request.UserLoginRequestDto;
import org.example.presentation.dto.request.UserPatchProfileRequestDto;
import org.example.presentation.dto.request.UserPutProfileRequestDto;
import org.example.presentation.dto.request.UserRegistrationRequestDto;
import org.example.presentation.dto.response.UserLoginResponseDto;
import org.example.presentation.dto.response.UserProfileResponseDto;
import org.example.presentation.dto.response.UserRegistrationResponseDto;

public interface UserService {

    UserRegistrationResponseDto registration(UserRegistrationRequestDto request);

    UserLoginResponseDto login(UserLoginRequestDto request);

    Optional<User> findByEmail(String email);

    List<UserRegistrationResponseDto> findAllUsers();

    UserRegistrationResponseDto findById(Long id);

    void deleteById(Long id);

    UserProfileResponseDto getUserProfile(String email);

    UserProfileResponseDto updateUserProfile(String email, UserPutProfileRequestDto request);

    UserProfileResponseDto patchUserProfile(String email, UserPatchProfileRequestDto request);

    UserProfileResponseDto updateUserRole(Long userId, String role);

    void updateEmailCredentials(Long userId, String emailUsername, String emailPassword);
}
