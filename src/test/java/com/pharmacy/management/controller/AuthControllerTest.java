package com.pharmacy.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmacy.management.dto.AuthRequest;
import com.pharmacy.management.model.Role;
import com.pharmacy.management.model.User;
import com.pharmacy.management.repository.UserRepository;
import com.pharmacy.management.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ContextConfiguration(classes = { AuthController.class,
        com.pharmacy.management.exception.GlobalExceptionHandler.class })
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setRole(Role.USER);

        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(org.springframework.security.core.userdetails.User.builder()
                        .username("testuser")
                        .password("password")
                        .roles("USER")
                        .build());
        when(jwtUtil.generateToken(any())).thenReturn("mock-jwt-token");
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Invalid"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid username or password"));
    }

    @Test
    void register_WhenUsernameTaken_ShouldReturnBadRequest() throws Exception {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username is already taken!"));
    }

    @Test
    void register_WhenAdminLimitReached_ShouldReturnBadRequest() throws Exception {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(5L);

        mockMvc.perform(post("/api/auth/register")
                .param("role", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot register more than 5 admins!"));
    }

    @Test
    void register_WithValidData_ShouldReturnOk() throws Exception {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }
}
