# Swagger Annotations Reference

This document provides a quick reference for the Swagger/OpenAPI annotations used in this project.

## Controller-Level Annotations

### @Tag
Defines a tag for grouping operations in Swagger UI.

```java
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {
    // ...
}
```

### @SecurityRequirement
Indicates that endpoints require authentication.

```java
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    // ...
}
```

## Method-Level Annotations

### @Operation
Describes an API operation with summary and detailed description.

```java
@Operation(
    summary = "Register a new user",
    description = "Creates a new user account with the provided credentials. Returns JWT tokens for authentication."
)
public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
    // ...
}
```

### @ApiResponses
Documents possible HTTP responses.

```java
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
    )
})
```

### @Parameter
Documents method parameters (path variables, query params, etc.).

```java
@Parameter(description = "User ID", required = true)
@PathVariable Long id
```

```java
@Parameter(description = "Page number (0-indexed)", example = "0")
@RequestParam(defaultValue = "0") int page
```

## DTO-Level Annotations

### @Schema (Class Level)
Describes the overall DTO/model.

```java
@Schema(description = "User registration request")
public class RegisterRequest {
    // ...
}
```

### @Schema (Field Level)
Documents individual fields with descriptions, examples, and requirements.

```java
@Schema(
    description = "User's email address",
    example = "john.doe@example.com",
    requiredMode = Schema.RequiredMode.REQUIRED
)
private String email;
```

```java
@Schema(
    description = "Page number (0-indexed)",
    example = "0"
)
private int pageNumber;
```

## Common Patterns

### Basic Endpoint Documentation

```java
@GetMapping("/{id}")
@Operation(summary = "Get user by ID")
public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
    // ...
}
```

### Comprehensive Endpoint Documentation

```java
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
public ResponseEntity<ApiResponse<AuthResponse>> register(
    @Valid @RequestBody RegisterRequest request
) {
    // ...
}
```

### File Upload Endpoint

```java
@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
@Operation(
    summary = "Upload a file",
    description = "Uploads a file to the server. The file is stored securely and associated with the authenticated user."
)
public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
    @Parameter(description = "File to upload", required = true)
    @RequestParam("file") MultipartFile file,
    @AuthenticationPrincipal User user
) {
    // ...
}
```

### Paginated Endpoint

```java
@GetMapping
@Operation(
    summary = "Get all users (Admin only)",
    description = "Retrieves a paginated list of all users. Only accessible by administrators."
)
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
    // ...
}
```

## DTO Documentation Patterns

### Request DTO

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User login request")
public class LoginRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(
        description = "User's email address",
        example = "john.doe@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;
    
    @NotBlank(message = "Password is required")
    @Schema(
        description = "User's password",
        example = "SecurePass123!",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;
}
```

### Response DTO

```java
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
    
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "Account creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}
```

### Generic Wrapper DTO

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {
    
    @Schema(description = "Indicates if the request was successful", example = "true")
    private boolean success;
    
    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;
    
    @Schema(description = "Response data")
    private T data;
    
    @Schema(description = "Error details (only present on failure)")
    private Object errors;
}
```

## Configuration

### OpenAPI Bean Configuration

```java
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot Starter API")
                        .description("Production-ready Spring Boot REST API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development server")
                ))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token obtained from login endpoint")));
    }
}
```

## Best Practices

1. **Always provide summaries** - Keep them concise (one line)
2. **Add descriptions for complex operations** - Explain what the endpoint does
3. **Document all parameters** - Include descriptions and examples
4. **Specify response codes** - Document success and error scenarios
5. **Use examples** - Help users understand expected values
6. **Mark required fields** - Use `requiredMode = Schema.RequiredMode.REQUIRED`
7. **Group related endpoints** - Use `@Tag` for logical grouping
8. **Document security requirements** - Use `@SecurityRequirement` where needed
9. **Keep descriptions user-friendly** - Write for API consumers, not developers
10. **Update documentation with code changes** - Keep them in sync

## Common Response Codes

| Code | Description | When to Use |
|------|-------------|-------------|
| 200 | OK | Successful GET, PUT, PATCH, DELETE |
| 201 | Created | Successful POST that creates a resource |
| 204 | No Content | Successful DELETE with no response body |
| 400 | Bad Request | Validation errors, malformed request |
| 401 | Unauthorized | Missing or invalid authentication |
| 403 | Forbidden | Authenticated but insufficient permissions |
| 404 | Not Found | Resource doesn't exist |
| 409 | Conflict | Resource already exists (e.g., duplicate email) |
| 500 | Internal Server Error | Unexpected server error |

## Additional Resources

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger Annotations Guide](https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations)
