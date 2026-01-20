package com.pharmacy.management.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private final String SECRET_KEY = "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXoxMjM0NTY="; // Base64 for a 32-byte key

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtUtil, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 86400000L);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertEquals("testuser", jwtUtil.extractUsername(token));
    }

    @Test
    void isTokenValid_WhenCorrectUser_ShouldReturnTrue() {
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        assertTrue(jwtUtil.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_WhenIncorrectUser_ShouldReturnFalse() {
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        UserDetails otherUser = new User("otheruser", "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        assertFalse(jwtUtil.isTokenValid(token, otherUser));
    }

    @Test
    void isTokenValid_WhenExpiredToken_ShouldReturnFalse() throws InterruptedException {
        // Set a very short expiration for this test
        ReflectionTestUtils.setField(jwtUtil, "jwtExpiration", 1L); // 1ms

        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String token = jwtUtil.generateToken(userDetails);

        // Wait for it to expire
        Thread.sleep(10);

        assertFalse(jwtUtil.isTokenValid(token, userDetails));
    }
}
