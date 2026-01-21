package org.example.service;

import java.util.List;
import java.util.Optional;
import org.example.dto.UserLoginRequestDto;
import org.example.dto.UserLoginResponseDto;
import org.example.dto.UserPatchProfileRequestDto;
import org.example.dto.UserProfileResponseDto;
import org.example.dto.UserPutProfileRequestDto;
import org.example.dto.UserRegistrationRequestDto;
import org.example.dto.UserRegistrationResponseDto;
import org.example.entity.User;

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
