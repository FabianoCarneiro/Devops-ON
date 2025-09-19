package com.example.demo.configuration;

import com.example.demo.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppUserTest {

    private User user;
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        when(user.getUsername()).thenReturn("testuser");
        when(user.getPassword()).thenReturn("testpass");
        when(user.getRole()).thenReturn("ADMIN");
        appUser = new AppUser(user);
    }

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = appUser.getAuthorities();
        assertNotNull(authorities);
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testGetPassword() {
        assertEquals("testpass", appUser.getPassword());
    }

    @Test
    void testGetUsername() {
        assertEquals("testuser", appUser.getUsername());
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(appUser.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(appUser.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(appUser.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(appUser.isEnabled());
    }
}