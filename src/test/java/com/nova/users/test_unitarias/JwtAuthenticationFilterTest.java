package com.nova.users.test_unitarias;


import com.nova.users.config.JwtService;
import com.nova.users.model.Role;
import com.nova.users.model.User;
import com.nova.users.model.UserDetailsImpl;
import com.nova.users.security.JwtAuthenticationFilter;
import com.nova.users.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_NoAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(any());
    }

    @Test
    void testDoFilterInternal_ValidToken() throws Exception {
        String jwt = "valid.jwt.token";
        String username = "test@example.com";
        User user = User.builder()
                .cedula("123456789")
                .correo(username)
                .contrasena("password")
                .rol(Role.ADMINISTRADOR)
                .build();
        UserDetails userDetails = new UserDetailsImpl(user);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtService.extractUsername(jwt)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(jwt, userDetails)).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testShouldNotFilter_AuthPath() {
        when(request.getServletPath()).thenReturn("/auth/login");
        assertTrue(jwtAuthenticationFilter.shouldNotFilter(request));
    }

    @Test
    void testShouldNotFilter_NonAuthPath() {
        when(request.getServletPath()).thenReturn("/api/users");
        assertFalse(jwtAuthenticationFilter.shouldNotFilter(request));
    }
}