package com.nova.users.test_unitarias;


import com.nova.users.model.User;
import com.nova.users.repository.UserRepository;
import com.nova.users.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsername_UserFound() {
        String email = "test@example.com";
        User user = User.builder()
                .cedula("123456789")
                .correo(email)
                .contrasena("password")
                .build();

        when(userRepository.findByCorreo(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        verify(userRepository).findByCorreo(email);
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        String email = "nonexistent@example.com";
        when(userRepository.findByCorreo(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        verify(userRepository).findByCorreo(email);
    }
}