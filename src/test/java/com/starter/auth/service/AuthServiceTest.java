package com.starter.auth.service;

import com.starter.auth.dto.AuthResponse;
import com.starter.auth.dto.LoginRequest;
import com.starter.auth.dto.RegisterRequest;
import com.starter.common.exception.BadRequestException;
import com.starter.security.JwtService;
import com.starter.user.entity.Role;
import com.starter.user.entity.User;
import com.starter.user.mapper.UserMapper;
import com.starter.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private AuthService authService;
    
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;
    
    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("password123")
                .build();
        
        loginRequest = LoginRequest.builder()
                .email("john@example.com")
                .password("password123")
                .build();
        
        user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .enabled(true)
                .build();
    }
    
    @Test
    void register_Success() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
        
        AuthResponse response = authService.register(registerRequest);
        
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void register_EmailAlreadyExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);
        
        assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken");
        
        AuthResponse response = authService.login(loginRequest);
        
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }
}
