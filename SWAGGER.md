# Swagger/OpenAPI Documentation

This application includes comprehensive Swagger/OpenAPI documentation for all REST API endpoints.

## Accessing Swagger UI

Once the application is running, you can access the Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **OpenAPI YAML**: http://localhost:8080/api-docs.yaml

## Features

### Interactive API Documentation
- Browse all available endpoints organized by tags (Authentication, User, File)
- View detailed request/response schemas
- See example values for all fields
- Understand validation rules and constraints

### Try It Out
- Test API endpoints directly from the browser
- No need for external tools like Postman or cURL
- Real-time request/response inspection

### Authentication Support
- Built-in JWT authentication support
- Easy token management via the "Authorize" button
- Automatic token inclusion in requests

## How to Use

### 1. Start the Application

```bash
./mvnw spring-boot:run
```

### 2. Open Swagger UI

Navigate to http://localhost:8080/swagger-ui.html in your browser.

### 3. Authenticate (for protected endpoints)

Most endpoints require authentication. Follow these steps:

1. **Register a new user**:
   - Expand the `Authentication` section
   - Click on `POST /api/v1/auth/register`
   - Click "Try it out"
   - Fill in the request body:
     ```json
     {
       "firstName": "John",
       "lastName": "Doe",
       "email": "john.doe@example.com",
       "password": "SecurePass123!"
     }
     ```
   - Click "Execute"
   - Copy the `accessToken` from the response

2. **Authorize**:
   - Click the "Authorize" button at the top of the page
   - Enter: `Bearer <your-access-token>` (replace with your actual token)
   - Click "Authorize" then "Close"

3. **Make authenticated requests**:
   - All subsequent requests will automatically include your JWT token
   - Try accessing protected endpoints like `GET /api/v1/users/{id}`

### 4. Explore Endpoints

#### Authentication Endpoints
- `POST /api/v1/auth/register` - Register a new user
- `POST /api/v1/auth/login` - Login and receive JWT tokens
- `POST /api/v1/auth/refresh` - Refresh access token

#### User Endpoints (Requires Authentication)
- `GET /api/v1/users/{id}` - Get user by ID
- `GET /api/v1/users` - Get all users (Admin only)
- `DELETE /api/v1/users/{id}` - Delete user (Admin only)

#### File Endpoints (Requires Authentication)
- `POST /api/v1/files/upload` - Upload a file
- `GET /api/v1/files/download/{fileName}` - Download a file
- `DELETE /api/v1/files/{fileName}` - Delete a file

## Configuration

Swagger configuration can be customized in `application.yml`:

```yaml
springdoc:
  api-docs:
    path: /api-docs                    # OpenAPI JSON endpoint
  swagger-ui:
    path: /swagger-ui.html             # Swagger UI path
    operations-sorter: method          # Sort operations by HTTP method
    tags-sorter: alpha                 # Sort tags alphabetically
```

Additional configuration options in `OpenApiConfig.java`:
- API title and description
- Version information
- Contact details
- License information
- Server URLs
- Security schemes

## API Response Format

All API responses follow a standard format:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { },
  "timestamp": "2024-01-15T10:30:00"
}
```

Error responses include additional details:

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "email": "Email is required",
    "password": "Password must be at least 8 characters"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

## Pagination

List endpoints support pagination with the following parameters:

- `page` - Page number (0-indexed, default: 0)
- `size` - Page size (default: 10)
- `sortBy` - Field to sort by (default: id)
- `sortDir` - Sort direction: ASC or DESC (default: ASC)

Example: `GET /api/v1/users?page=0&size=20&sortBy=email&sortDir=ASC`

Paginated responses include metadata:

```json
{
  "content": [...],
  "pageNumber": 0,
  "pageSize": 20,
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false
}
```

## Schema Annotations

All DTOs are annotated with `@Schema` for better documentation:

- Field descriptions
- Example values
- Required/optional indicators
- Validation constraints

## Security

### JWT Authentication
- Access tokens expire after 24 hours
- Refresh tokens expire after 7 days
- Use the refresh endpoint to obtain new access tokens

### Role-Based Access Control
- `USER` role - Standard user access
- `ADMIN` role - Administrative access (user management)

## Tips

1. **Use the "Authorize" button** - It's easier than manually adding headers
2. **Check response schemas** - Understand what data you'll receive
3. **Review validation rules** - See what's required before making requests
4. **Try example values** - Pre-filled examples help you get started quickly
5. **Export OpenAPI spec** - Download the JSON/YAML for use with other tools

## Troubleshooting

### 401 Unauthorized
- Ensure you've clicked "Authorize" and entered a valid token
- Check if your token has expired (24 hours for access tokens)
- Use the refresh endpoint to get a new access token

### 403 Forbidden
- You're authenticated but don't have permission
- Check if the endpoint requires ADMIN role
- Verify your user's role in the database

### 400 Bad Request
- Review the error response for validation details
- Check required fields and format constraints
- Ensure request body matches the schema

## Production Considerations

For production deployments:

1. **Disable Swagger UI** (optional):
   ```yaml
   springdoc:
     swagger-ui:
       enabled: false
   ```

2. **Restrict access** - Add security rules in `SecurityConfig.java`

3. **Update server URLs** - Configure production URLs in `OpenApiConfig.java`

4. **Customize branding** - Update API title, description, and contact info

## Additional Resources

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
