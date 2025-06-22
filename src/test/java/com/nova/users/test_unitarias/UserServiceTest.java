package com.nova.users.test_unitarias;

import com.nova.users.model.User;
import com.nova.users.repository.UserRepository;
import com.nova.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        // Arrange
        User user = new User();
        user.setCorreo("test@test.com");
        when(userRepository.findByCorreo("test@test.com")).thenReturn(Optional.of(user));

        // Act
        var result = userService.loadUserByUsername("test@test.com");

        // Assert
        assertNotNull(result);
        assertEquals("test@test.com", ((User) result).getCorreo());
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // Arrange
        when(userRepository.findByCorreo("nonexistent@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexistent@test.com");
        });
    }

    @Test
    void encodePassword_ValidPassword_ReturnsEncoded() {
        // Arrange
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");

        // Act
        String result = userService.encodePassword("rawPassword");

        // Assert
        assertEquals("encodedPassword", result);
    }
}