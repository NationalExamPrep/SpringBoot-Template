package com.starter.user.dto;

import com.starter.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User information response")
public class UserResponse {
    
    @Schema(description = "User ID", example = "1")
    private Long id;
    
    @Schema(description = "User's first name", example = "John")
    private String firstName;
    
    @Schema(description = "User's last name", example = "Doe")
    private String lastName;
    
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "User's role", example = "USER")
    private Role role;
    
    @Schema(description = "Whether the user account is enabled", example = "true")
    private boolean enabled;
    
    @Schema(description = "Account creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Last update timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}
