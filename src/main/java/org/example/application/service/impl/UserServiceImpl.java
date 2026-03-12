package org.example.application.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.application.mapper.UserMapper;
import org.example.application.service.UserService;
import org.example.domain.entity.Role;
import org.example.domain.entity.User;
import org.example.domain.entity.status.RoleName;
import org.example.domain.exception.RegistrationException;
import org.example.domain.exception.UserNotFoundException;
import org.example.domain.exception.UserRoleNotFoundException;
import org.example.infrastructure.exception.authentication.AuthenticationException;
import org.example.infrastructure.integration.EmailCredentialsCryptoService;
import org.example.infrastructure.repository.RoleRepository;
import org.example.infrastructure.repository.UserRepository;
import org.example.infrastructure.security.jwt.JwtUtil;
import org.example.presentation.dto.request.UserLoginRequestDto;
import org.example.presentation.dto.request.UserPatchProfileRequestDto;
import org.example.presentation.dto.request.UserPutProfileRequestDto;
import org.example.presentation.dto.request.UserRegistrationRequestDto;
import org.example.presentation.dto.response.UserLoginResponseDto;
import org.example.presentation.dto.response.UserProfileResponseDto;
import org.example.presentation.dto.response.UserRegistrationResponseDto;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final EmailCredentialsCryptoService cryptoService;

    @Override
    @Transactional
    public UserRegistrationResponseDto registration(UserRegistrationRequestDto request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RegistrationException("User with email already exists!: " + request.email());
        }

        Role userRole = roleRepository.findByRoleName(RoleName.USER)
                        .orElseThrow(() -> new UserRoleNotFoundException(
                                "The USER role not found!  "));

        User user = userMapper.toModel(request);

        user.setPassword(passwordEncoder.encode(request.password()));

        user.addRole(userRole);

        User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto request) {

        User user = userRepository.findByEmail(request.email())
                        .orElseThrow(() -> new AuthenticationException(
                                "Invalid email or password!"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthenticationException("Invalid email or password!");
        }

        String token = jwtUtil.generateToken(
                        user.getEmail(),
                        user.getRoles().stream().map(role -> role.getRoleName().name()).toList());

        return new UserLoginResponseDto(token);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserRegistrationResponseDto> findAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Override
    public UserRegistrationResponseDto findById(Long id) {
        User user = userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException("User not found with id!:"
                                + id));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id!: " + id);
        }

        userRepository.deleteById(id);
    }

    @Override
    public UserProfileResponseDto getUserProfile(String email) {

        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException("User not found! " + email));

        return userMapper.toProfileDto(user);
    }

    @Override
    @Transactional
    public UserProfileResponseDto updateUserProfile(String email,
                                                    UserPutProfileRequestDto request) {

        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException("User not found!: " + email));

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        User savedUser = userRepository.save(user);

        return userMapper.toProfileDto(savedUser);
    }

    @Override
    @Transactional
    public UserProfileResponseDto patchUserProfile(String email,
                                                   UserPatchProfileRequestDto request) {

        User user = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException("User not found!: " + email));

        if (request.password() != null && !request.password().isBlank()) {
            String encodedPassword = passwordEncoder.encode(request.password());
            user.setPassword(encodedPassword);
        }
        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.email() != null) {
            user.setEmail(request.email());
        }
        if (request.emailUsername() != null) {
            user.setEmailUsername(request.emailUsername());
        }
        if (request.emailPassword() != null) {
            user.setEmailPassword(cryptoService.encrypt(request.emailPassword()));
        }
        if (request.telegramChatId() != null) {
            user.setTelegramChatId(request.telegramChatId());
        }

        User savedUser = userRepository.save(user);

        return userMapper.toProfileDto(savedUser);
    }

    @Override
    @Transactional
    public UserProfileResponseDto updateUserRole(Long userId, String role) {

        RoleName roleName = RoleName.valueOf(role.toUpperCase());

        User user =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User not found!" + userId));

        Role userRole = roleRepository.findByRoleName(roleName)
                        .orElseThrow(() -> new UserRoleNotFoundException("Role " + roleName
                                + " not found!"));

        user.getRoles().clear();
        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);

        return userMapper.toProfileDto(savedUser);
    }

    @Transactional
    public void updateEmailCredentials(Long userId, String emailUsername, String emailPassword)
            throws UsernameNotFoundException {

        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found!: "
                                + userId));

        user.setEmailUsername(emailUsername);
        user.setEmailPassword(cryptoService.encrypt(emailPassword));
    }
}
