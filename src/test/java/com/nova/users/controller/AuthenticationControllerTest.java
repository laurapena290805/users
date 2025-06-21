package com.nova.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nova.users.config.JwtService;
import com.nova.users.config.SecurityConfig;
import com.nova.users.dto.AuthenticationRequest;
import com.nova.users.dto.RegisterRequest;
import com.nova.users.model.Role;
import com.nova.users.model.User;
import com.nova.users.repository.UserRepository;
import com.nova.users.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthenticationController.class)
@Import(SecurityConfig.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_shouldReturnToken() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setCorreo("test@test.com");
        request.setContrasena("password");

        User user = new User();
        user.setCorreo("test@test.com");
        user.setRol(Role.CLIENTE);

        when(userRepository.findByCorreo(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("fake-token");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-token"));
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("test@test.com", "password");

        User user = new User();
        user.setCorreo("test@test.com");
        user.setContrasena("encodedPassword");
        user.setRol(Role.CLIENTE);

        when(userDetailsService.loadUserByUsername(any())).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("fake-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-token"));
    }
} 