package com.pharmacy.management.security;

import com.pharmacy.management.model.Role;
import com.pharmacy.management.model.User;
import com.pharmacy.management.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.USER);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        assertEquals("testuser", result.getUsername());
        assertEquals("password", result.getPassword());
        assertTrue(result.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername("unknown"));
    }
}
