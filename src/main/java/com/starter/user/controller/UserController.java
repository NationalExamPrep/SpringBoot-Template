package com.starter.user.controller;

import com.starter.common.dto.ApiResponse;
import com.starter.common.dto.PageResponse;
import com.starter.user.dto.UserResponse;
import com.starter.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User", description = "User management APIs")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves user information by their unique identifier. Requires authentication."
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(
            responseCode = "200",
            description = "User found",
            content = @Content(schema = @Schema(implementation = UserResponse.class))
        ),
        @SwaggerApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        ),
        @SwaggerApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content
        )
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
        @Parameter(description = "User ID", required = true)
        @PathVariable Long id
    ) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get all users (Admin only)",
        description = "Retrieves a paginated list of all users. Only accessible by administrators."
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = PageResponse.class))
        ),
        @SwaggerApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content
        ),
        @SwaggerApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin role required",
            content = @Content
        )
    })
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field", example = "id")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "ASC")
            @RequestParam(defaultValue = "ASC") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("DESC") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        PageResponse<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Delete user (Admin only)",
        description = "Permanently deletes a user account. Only accessible by administrators."
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(
            responseCode = "200",
            description = "User deleted successfully",
            content = @Content
        ),
        @SwaggerApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        ),
        @SwaggerApiResponse(
            responseCode = "401",
            description = "Unauthorized",
            content = @Content
        ),
        @SwaggerApiResponse(
            responseCode = "403",
            description = "Forbidden - Admin role required",
            content = @Content
        )
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(
        @Parameter(description = "User ID to delete", required = true)
        @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}
