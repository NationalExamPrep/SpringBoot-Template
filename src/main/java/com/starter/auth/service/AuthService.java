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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .enabled(true)
                .build();
        
        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());
        
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toResponse(user))
                .build();
    }
    
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));
        
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        log.info("User logged in successfully: {}", user.getEmail());
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toResponse(user))
                .build();
    }
    
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException("User not found"));
        
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadRequestException("Invalid refresh token");
        }
        
        String newAccessToken = jwtService.generateToken(user);
        
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .user(userMapper.toResponse(user))
                .build();
    }
}
