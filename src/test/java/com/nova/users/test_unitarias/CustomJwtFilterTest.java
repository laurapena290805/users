package com.nova.users.test_unitarias;

import com.nova.users.config.CustomJwtFilter;
import com.nova.users.security.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

class CustomJwtFilterTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private CustomJwtFilter customJwtFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoFilterInternal_ActuatorPath() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/actuator/health");

        customJwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtAuthenticationFilter, never()).doFilter(any(), any(), any());
    }

    @Test
    void testDoFilterInternal_NonActuatorPath() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/users");

        customJwtFilter.doFilterInternal(request, response, filterChain);

        verify(jwtAuthenticationFilter).doFilter(request, response, filterChain);
        verify(filterChain, never()).doFilter(request, response);
    }
}