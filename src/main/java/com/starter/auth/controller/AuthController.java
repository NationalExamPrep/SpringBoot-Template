package com.starter.auth.controller;

import com.starter.auth.dto.AuthResponse;
import com.starter.auth.dto.LoginRequest;
import com.starter.auth.dto.RegisterRequest;
import com.starter.auth.service.AuthService;
import com.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided credentials. Returns JWT tokens for authentication."
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(
            responseCode = "201",
            description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @SwaggerApiResponse(
            responseCode = "400",
            description = "Invalid input or email already exists",
            content = @Content
        )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }
    
    @PostMapping("/login")
    @Operation(
        summary = "Login user",
        description = "Authenticates a user with email and password. Returns JWT access and refresh tokens."
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(
            responseCode = "200",
            description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @SwaggerApiResponse(
            responseCode = "401",
            description = "Invalid credentials",
            content = @Content
        )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
    
    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh access token",
        description = "Generates a new access token using a valid refresh token"
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(
            responseCode = "200",
            description = "Token refreshed successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @SwaggerApiResponse(
            responseCode = "401",
            description = "Invalid or expired refresh token",
            content = @Content
        )
    })
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
        @Parameter(description = "Refresh token", required = true)
        @RequestParam String refreshToken
    ) {
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }
}
