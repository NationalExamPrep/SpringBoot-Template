# API Examples

This document provides example API requests for testing the Spring Boot Starter application.

## Authentication

### Register a New User

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john@example.com",
      "role": "USER",
      "enabled": true
    }
  }
}
```

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Refresh Token

```bash
curl -X POST "http://localhost:8080/api/v1/auth/refresh?refreshToken=YOUR_REFRESH_TOKEN"
```

## User Management

### Get User by ID

```bash
curl -X GET http://localhost:8080/api/v1/users/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Get All Users (Admin Only)

```bash
curl -X GET "http://localhost:8080/api/v1/users?page=0&size=10&sortBy=id&sortDir=ASC" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Delete User (Admin Only)

```bash
curl -X DELETE http://localhost:8080/api/v1/users/1 \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## File Management

### Upload File

```bash
curl -X POST http://localhost:8080/api/v1/files/upload \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -F "file=@/path/to/your/file.pdf"
```

**Response:**
```json
{
  "success": true,
  "message": "File uploaded successfully",
  "data": {
    "id": 1,
    "fileName": "550e8400-e29b-41d4-a716-446655440000.pdf",
    "originalFileName": "file.pdf",
    "contentType": "application/pdf",
    "fileSize": 102400,
    "downloadUrl": "http://localhost:8080/api/v1/files/download/550e8400-e29b-41d4-a716-446655440000.pdf",
    "uploadedAt": "2024-05-24T10:30:00"
  }
}
```

### Download File

```bash
curl -X GET http://localhost:8080/api/v1/files/download/550e8400-e29b-41d4-a716-446655440000.pdf \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -o downloaded-file.pdf
```

### Delete File

```bash
curl -X DELETE http://localhost:8080/api/v1/files/550e8400-e29b-41d4-a716-446655440000.pdf \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## Health Check

```bash
curl -X GET http://localhost:8080/actuator/health
```

## Using Postman

1. Import the API endpoints into Postman
2. Create an environment with:
   - `baseUrl`: http://localhost:8080
   - `accessToken`: (will be set after login)
3. For authenticated requests, add header:
   - Key: `Authorization`
   - Value: `Bearer {{accessToken}}`

## Testing with HTTPie

### Register
```bash
http POST localhost:8080/api/v1/auth/register \
  firstName=John \
  lastName=Doe \
  email=john@example.com \
  password=password123
```

### Login
```bash
http POST localhost:8080/api/v1/auth/login \
  email=john@example.com \
  password=password123
```

### Get User (with auth)
```bash
http GET localhost:8080/api/v1/users/1 \
  "Authorization: Bearer YOUR_ACCESS_TOKEN"
```
