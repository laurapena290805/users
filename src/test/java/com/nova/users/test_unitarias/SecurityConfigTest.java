package com.nova.users.test_unitarias;

import com.nova.users.config.JwtService;
import com.nova.users.model.Role;
import com.nova.users.model.User;
import com.nova.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @Test
    void publicEndpoints_ShouldBeAccessible() throws Exception {
        // Configurar mocks
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("mockToken");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "cedula": "123456789",
                        "nombre": "Test",
                        "apellido": "User",
                        "correo": "test@test.com",
                        "contrasena": "password",
                        "rol": "CLIENTE"
                    }"""))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void adminEndpoints_WithAdminRole_ShouldBeAccessible() throws Exception {
        // Configurar mocks
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/users/admin/repartidores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "cedula": "987654321",
                        "nombre": "Repartidor",
                        "apellido": "Test",
                        "correo": "repartidor@test.com",
                        "contrasena": "password",
                        "rol": "REPARTIDOR"
                    }"""))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void adminEndpoints_WithNonAdminRole_ShouldBeForbidden() throws Exception {
        mockMvc.perform(post("/users/admin/repartidores")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}