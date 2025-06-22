package com.nova.users.test_unitarias;

import com.nova.users.model.Role;
import com.nova.users.model.User;
import com.nova.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles = "CLIENTE")
    void getAllUsers_NonAdminAccess_ReturnsForbidden() throws Exception {
        // Configura usuario no administrador
        User regularUser = new User();
        regularUser.setCorreo("user@test.com");
        regularUser.setRol(Role.CLIENTE);

        when(userRepository.findByCorreo("user@test.com")).thenReturn(Optional.of(regularUser));

        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void getAllUsers_AdminAccess_ReturnsOk() throws Exception {
        // Configura usuario administrador
        User adminUser = new User();
        adminUser.setCorreo("admin@test.com");
        adminUser.setRol(Role.ADMINISTRADOR);

        when(userRepository.findByCorreo("admin@test.com")).thenReturn(Optional.of(adminUser));
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(adminUser)));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_OwnProfile_ReturnsOk() throws Exception {
        // Configura usuario
        User testUser = new User();
        testUser.setCedula("456");
        testUser.setCorreo("user@test.com");
        testUser.setRol(Role.CLIENTE);

        when(userRepository.findByCorreo("user@test.com")).thenReturn(Optional.of(testUser));
        when(userRepository.findById("456")).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/456")
                        .with(user("user@test.com").roles("CLIENTE")))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_OtherProfile_ReturnsForbidden() throws Exception {
        // Configura usuarios
        User currentUser = new User();
        currentUser.setCorreo("user1@test.com");
        currentUser.setRol(Role.CLIENTE);

        User targetUser = new User();
        targetUser.setCedula("789");
        targetUser.setCorreo("user2@test.com");

        when(userRepository.findByCorreo("user1@test.com")).thenReturn(Optional.of(currentUser));
        when(userRepository.findById("789")).thenReturn(Optional.of(targetUser));

        mockMvc.perform(get("/users/789")
                        .with(user("user1@test.com").roles("CLIENTE")))
                .andExpect(status().isForbidden());
    }
}