package com.starter.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.starter.user.dto.UserResponse;
import com.starter.user.entity.Role;
import com.starter.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private UserService userService;
    
    private UserResponse userResponse;
    private UUID testUserId;
    
    @BeforeEach
    void setUp() {
        testUserId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        userResponse = UserResponse.builder()
                .id(testUserId)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .role(Role.USER)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    @WithMockUser
    void getUserById_Success() throws Exception {
        when(userService.getUserById(any(UUID.class))).thenReturn(userResponse);
        
        mockMvc.perform(get("/api/v1/users/" + testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }
    
    @Test
    void getUserById_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/users/" + testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
