package org.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Set;
import org.example.domain.entity.Role;
import org.example.domain.entity.User;
import org.example.domain.entity.status.RoleName;
import org.example.infrastructure.repository.RoleRepository;
import org.example.infrastructure.repository.UserRepository;
import org.example.infrastructure.security.jwt.JwtUtil;
import org.example.presentation.dto.request.UserPatchProfileRequestDto;
import org.example.presentation.dto.request.UserPutProfileRequestDto;
import org.example.presentation.dto.request.UserUpdateRoleRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    private User savedUser;

    private Role userRole;
    @MockBean
    private TextEncryptor textEncryptor;

    @BeforeEach
    void setUp() {

        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role adminRole = new Role();
        adminRole.setRoleName(RoleName.ADMIN);
        roleRepository.save(adminRole);

        Role userRole = new Role();
        userRole.setRoleName(RoleName.USER);
        roleRepository.save(userRole);

        User user = new User();
        user.setEmail("testlocal@email.com");
        user.setPassword(passwordEncoder.encode("secure-password"));
        user.setFirstName("Bob");
        user.setLastName("Johnson");
        user.setRoles(Set.of(adminRole));
        user.setEmailUsername(textEncryptor.encrypt("testexternal@email.com"));
        user.setEmailPassword(textEncryptor.encrypt("encoded-password"));
        user.setTelegramChatId(123456789L);
        savedUser = userRepository.save(user);
    }

    private UserPutProfileRequestDto putRequest() {

        return new UserPutProfileRequestDto("Alice", "Cooper");
    }

    private UserPatchProfileRequestDto patchRequest() {

        return new UserPatchProfileRequestDto(
                "updatedlocal@email.com",
                "updated-secure-password",
                "Charlie",
                "Edison",
                "updatedUsername@mail.com",
                "updated-encoded-password",
                123456789L);
    }

    private UserUpdateRoleRequestDto updateRoleRequest() {

        return new UserUpdateRoleRequestDto("USER");
    }

    @Test
    @DisplayName("GET /users/me - return current user profile")
    @WithMockUser(
            username = "testlocal@email.com",
            roles = {"ADMIN", "USER"})
    void getUserProfile() throws Exception {

        MvcResult result = mockMvc.perform(get("/users/me")).andExpect(status().isOk()).andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());

        assertThat(root.get("email").asText()).isEqualTo("testlocal@email.com");
        assertThat(root.get("roles").get(0).asText()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("PUT /users/me - return update user`s profile")
    @WithMockUser(
            username = "testlocal@email.com",
            roles = {"ADMIN", "USER"})
    void updateUserProfile() throws Exception {

        MvcResult result =
                mockMvc
                        .perform(
                                put("/users/me")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(putRequest())))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());

        assertThat(root.get("firstName").asText()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("PATCH /users/me - return partially update user`s profile")
    @WithMockUser(username = "testlocal@email.com", roles = "USER")
    void patchUserProfile() throws Exception {

        MvcResult result =
                mockMvc
                        .perform(
                                patch("/users/me")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(patchRequest())))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());

        assertThat(root.get("firstName").asText()).isEqualTo("Charlie");
    }

    @Test
    @DisplayName("PUT /users/{id}/role - admin updated user role")
    @WithMockUser(username = "testlocal@email.com", roles = "ADMIN")
    void updateUserRole() throws Exception {

        MvcResult result =
                mockMvc
                        .perform(
                                put("/users/{id}/role", savedUser.getId())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(updateRoleRequest())))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());

        assertThat(root.get("roles").get(0).asText()).isEqualTo("USER");
    }
}
