package com.nova.users.test_unitarias;

import com.nova.users.config.JwtService;
import com.nova.users.model.Role;
import com.nova.users.model.User;
import com.nova.users.model.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UserDetailsImpl userDetails;
    private String token;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        User user = User.builder()
                .cedula("123456789")
                .correo("test@example.com")
                .contrasena("password")
                .rol(Role.ADMINISTRADOR)
                .build();
        userDetails = new UserDetailsImpl(user);
        token = jwtService.generateToken(userDetails);
    }

    @Test
    void testExtractUsername() {
        String username = jwtService.extractUsername(token);
        assertEquals("test@example.com", username);
    }

    @Test
    void testExtractExpiration() {
        Date expiration = jwtService.extractExpiration(token);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void testExtractClaim() {
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        assertEquals("test@example.com", subject);
    }

    @Test
    void testGenerateToken() {
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void testIsTokenValid() {
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void testIsTokenExpired() {
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void testExtractAllClaims() {
        Claims claims = jwtService.extractAllClaims(token);
        assertNotNull(claims);
        assertEquals("test@example.com", claims.getSubject());
        assertTrue(claims.get("authorities") instanceof List);
    }

    @Test
    void testGetSignInKey() {
        assertNotNull(jwtService.getSignInKey());
    }
}