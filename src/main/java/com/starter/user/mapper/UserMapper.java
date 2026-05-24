package com.starter.user.mapper;

import com.starter.user.dto.UserResponse;
import com.starter.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
