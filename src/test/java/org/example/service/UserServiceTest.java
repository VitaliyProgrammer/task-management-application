package org.example.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.example.application.mapper.UserMapper;
import org.example.application.service.impl.UserServiceImpl;
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
import org.example.presentation.dto.request.UserRegistrationRequestDto;
import org.example.presentation.dto.response.UserLoginResponseDto;
import org.example.presentation.dto.response.UserProfileResponseDto;
import org.example.presentation.dto.response.UserRegistrationResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailCredentialsCryptoService cryptoService;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user;

    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = createUserRole();
        user = createUser();
    }

    public static Role createUserRole() {

        Role role = new Role();
        role.setRoleName(RoleName.USER);

        return role;
    }

    public static User createUser() {

        User user = new User();

        user.setId(1L);
        user.setEmail("test@email.com");
        user.setPassword("encoded-password");
        user.setFirstName("Bob");
        user.setLastName("Johnson");
        user.setRoles(new HashSet<>());

        return user;
    }

    public static UserRegistrationRequestDto registrationRequest() {

        return new UserRegistrationRequestDto(
                "test@email.com", "encoded-password", "encoded-password", "Bob", "Johnson");
    }

    public static UserRegistrationResponseDto registrationResponse() {

        return new UserRegistrationResponseDto(1L, "Bob", "Johnson", "test@email.com");
    }

    public static UserProfileResponseDto userProfileResponse() {

        return new UserProfileResponseDto(
                1L, "test@email.com", "Bob", "Johnson", Set.of("USER"), null, null, null);
    }

    public static UserLoginRequestDto loginRequest() {

        return new UserLoginRequestDto("test@email.com", "encoded-password");
    }

    public static UserPatchProfileRequestDto patchAllFieldsRequest() {

        return new UserPatchProfileRequestDto(
                "updatedEmail",
                "updatedPassword",
                "updatedFirstName",
                "updatedLastName",
                "updatedUsername",
                "updatedEmailPassword",
                2222222L);
    }

    public static UserPatchProfileRequestDto patchPasswordOnly() {

        return new UserPatchProfileRequestDto(null, "new-password", null, null, null, null, null);
    }

    @Test
    @DisplayName("Register user successfully")
    void registration_success() {

        when(userRepository.existsByEmail(registrationRequest().email())).thenReturn(false);
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(Optional.of(userRole));
        when(userMapper.toModel(registrationRequest())).thenReturn(user);
        when(passwordEncoder.encode(registrationRequest().password())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(registrationResponse());

        UserRegistrationResponseDto responseRequest =
                userServiceImpl.registration(registrationRequest());

        assertNotNull(responseRequest);
        assertEquals(responseRequest.email(), registrationRequest().email());

        assertEquals("encoded", user.getPassword());
        assertTrue(user.getRoles().contains(userRole));

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("The exception if email is already exists")
    void registration_emailAlreadyExists() {

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(
                RegistrationException.class, () -> userServiceImpl.registration(registrationRequest()));
    }

    @Test
    void registration_userRoleNotFound() {
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(Optional.empty());

        assertThrows(
                UserRoleNotFoundException.class, () -> userServiceImpl.registration(registrationRequest()));
    }

    @Test
    @DisplayName("Login successfully")
    void login_success() {

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequest().password(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(anyString(), anyList())).thenReturn("jwt-token");

        UserLoginResponseDto response = userServiceImpl.login(loginRequest());

        assertEquals("jwt-token", response.token());
    }

    @Test
    @DisplayName("Login is failing: wrong password")
    void login_wrongPassword() {

        when(userRepository.findByEmail(loginRequest().email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> userServiceImpl.login(loginRequest()));
    }

    @Test
    @DisplayName("User is not exists")
    void login_userNotFound() {
        when(userRepository.findByEmail(loginRequest().email())).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class, () -> userServiceImpl.login(loginRequest()));
    }

    @Test
    @DisplayName("Get user profile - successfully")
    void getUserProfile_success() {

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userMapper.toProfileDto(user)).thenReturn(userProfileResponse());

        UserProfileResponseDto result = userServiceImpl.getUserProfile(user.getEmail());

        assertEquals(userProfileResponse().firstName(), result.firstName());
        assertEquals(userProfileResponse().lastName(), result.lastName());
        assertEquals(userProfileResponse().email(), result.email());
        assertTrue(result.roles().contains(RoleName.USER.name()));
    }

    @Test
    @DisplayName("Get user profile - user is not exists")
    void getUserProfile_userNotFound() {

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class, () -> userServiceImpl.getUserProfile(user.getEmail()));
    }

    @Test
    @DisplayName("User by id is deleted")
    void deleteById_success() {

        when(userRepository.existsById(user.getId())).thenReturn(true);

        userServiceImpl.deleteById(user.getId());

        verify(userRepository).deleteById(user.getId());
    }

    @Test
    @DisplayName("User is not exists")
    void deleteById_userNotFound() {

        when(userRepository.existsById(user.getId())).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userServiceImpl.deleteById(user.getId()));
    }

    @Test
    @DisplayName("User is exists with specific id")
    void findById_success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(registrationResponse());

        UserRegistrationResponseDto dto = userServiceImpl.findById(user.getId());

        assertEquals("test@email.com", dto.email());
    }

    @Test
    @DisplayName("User is not exists with specific id")
    void findById_notFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userServiceImpl.findById(user.getId()));
    }

    @Test
    @DisplayName("Find by email - optional present")
    void findByEmail_present() {

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Optional<User> result = userServiceImpl.findByEmail(user.getEmail());

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    void findAllUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(registrationResponse());

        List<UserRegistrationResponseDto> users = userServiceImpl.findAllUsers();

        assertEquals(1, users.size());
    }

    @Test
    @DisplayName("Patch user profile - update password of user")
    void patchUserProfile_updatePasswordOnly() {

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(patchPasswordOnly().password())).thenReturn("new-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userServiceImpl.patchUserProfile(user.getEmail(), patchPasswordOnly());

        assertEquals("new-password", user.getPassword());
        verify(userRepository).save(user);
        verify(passwordEncoder).encode(patchPasswordOnly().password());
    }

    @Test
    @DisplayName("Patch user profile - updated all fields")
    void patchUserProfile_updateAllFields() {

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(patchAllFieldsRequest().password())).thenReturn("updatedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userServiceImpl.patchUserProfile(user.getEmail(), patchAllFieldsRequest());

        assertEquals(patchAllFieldsRequest().email(), user.getEmail());
        assertEquals(patchAllFieldsRequest().password(), user.getPassword());
        assertEquals(patchAllFieldsRequest().firstName(), user.getFirstName());
        assertEquals(patchAllFieldsRequest().lastName(), user.getLastName());

        verify(passwordEncoder).encode(patchAllFieldsRequest().password());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Patch user profile - user not found")
    void patchUserProfile_userNotFound() {

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userServiceImpl.patchUserProfile(user.getEmail(), patchAllFieldsRequest()));
    }

    @Test
    @DisplayName("Update email credentials - user is exists")
    void updateEmailCredentials_success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(cryptoService.encrypt("email-pass")).thenReturn("encrypted-email-pass");

        userServiceImpl.updateEmailCredentials(user.getId(), "username", "email-pass");

        assertEquals("username", user.getEmailUsername());
        assertEquals("encrypted-email-pass", user.getEmailPassword());
    }

    @Test
    @DisplayName("User`s role is updated successfully")
    void updateUserRole_success() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName(RoleName.ADMIN)).thenReturn(Optional.of(new Role()));
        when(userRepository.save(user)).thenReturn(user);

        userServiceImpl.updateUserRole(user.getId(), "ADMIN");

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("User`s role is not found")
    void updateUserRole_roleNotFound() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleName(RoleName.USER)).thenReturn(Optional.empty());

        assertThrows(
                UserRoleNotFoundException.class,
                () -> userServiceImpl.updateUserRole(user.getId(), "USER"));
    }

    @Test
    @DisplayName("User is not found")
    void updateUserRole_userNotFound() {

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userServiceImpl.updateUserRole(user.getId(), RoleName.ADMIN.name()));

        verify(userRepository, never()).save(any());
    }
}
