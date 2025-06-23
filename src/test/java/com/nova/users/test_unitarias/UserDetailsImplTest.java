package com.nova.users.test_unitarias;

import com.nova.users.model.Role;
import com.nova.users.model.User;
import com.nova.users.model.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserDetailsImplTest {

    private UserDetailsImpl userDetails;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .cedula("123456789")
                .correo("test@example.com")
                .contrasena("password")
                .rol(Role.ADMINISTRADOR)
                .build();
        userDetails = new UserDetailsImpl(user);
    }

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertEquals("ROLE_ADMINISTRADOR", authorities.iterator().next().getAuthority());
    }

    @Test
    void testGetPassword() {
        assertEquals("password", userDetails.getPassword());
    }

    @Test
    void testGetUsername() {
        assertEquals("test@example.com", userDetails.getUsername());
    }

    @Test
    void testAccountNonExpired() {
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    void testAccountNonLocked() {
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void testCredentialsNonExpired() {
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void testEnabled() {
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void testGetUser() {
        assertEquals(user, userDetails.getUser());
    }
}
