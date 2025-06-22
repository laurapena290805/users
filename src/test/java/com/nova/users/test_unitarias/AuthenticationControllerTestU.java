package com.nova.users.test_unitarias;

import com.nova.users.config.JwtService;
import com.nova.users.controller.AuthenticationController;
import com.nova.users.dto.AuthenticationRequest;
import com.nova.users.dto.AuthenticationResponse;
import com.nova.users.dto.RegisterRequest;
import com.nova.users.model.Role;
import com.nova.users.model.User;
import com.nova.users.repository.UserRepository;
import com.nova.users.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationControllerTestU {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_WithValidRegisterRequest_ReturnsAuthenticationResponse() {
        // Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setCedula("1234567890");
        registerRequest.setNombre("Test");
        registerRequest.setApellido("User");
        registerRequest.setCorreo("test@example.com");
        registerRequest.setContrasena("password123");

        when(userRepository.findByCorreo("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("testToken");

        // Act
        ResponseEntity<AuthenticationResponse> response = authenticationController.register(registerRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testToken", response.getBody().getToken());

        verify(userRepository).save(argThat(user ->
                user.getCorreo().equals("test@example.com") &&
                        user.getRol() == Role.CLIENTE));
    }

    @Test
    void login_WithValidAuthenticationRequest_ReturnsAuthenticationResponse() {
        // Arrange
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setCorreo("user@test.com");
        authRequest.setContrasena("password123");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("testToken");

        // Act
        ResponseEntity<AuthenticationResponse> response = authenticationController.login(authRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("testToken", response.getBody().getToken());

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("user@test.com", "password123"));
    }


    @Test
    void register_WithExistingEmail_ThrowsException() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setCorreo("existing@test.com");
        when(userRepository.findByCorreo("existing@test.com")).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authenticationController.register(request);
        });
    }

    @Test
    void login_WithInvalidCredentials_ThrowsException() {
        // Arrange
        AuthenticationRequest request = new AuthenticationRequest();
        request.setCorreo("user@test.com");
        request.setContrasena("wrongpass");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authenticationController.login(request);
        });
    }

}