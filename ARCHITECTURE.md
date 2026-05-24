# Architecture Documentation

## Overview

This Spring Boot Starter follows a **Layered Architecture** pattern with clear separation of concerns, making it maintainable, testable, and scalable.

## Architecture Layers

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│    (Controllers, DTOs, Filters)         │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│          Business Layer                 │
│      (Services, Mappers, Logic)         │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│         Persistence Layer               │
│    (Repositories, Entities, DB)         │
└─────────────────────────────────────────┘
```

## Package Structure

```
com.starter/
├── auth/                    # Authentication module
│   ├── controller/          # Auth endpoints
│   ├── dto/                 # Auth request/response objects
│   └── service/             # Auth business logic
│
├── user/                    # User management module
│   ├── controller/          # User endpoints
│   ├── dto/                 # User DTOs
│   ├── entity/              # User entity
│   ├── mapper/              # Entity-DTO mappers
│   ├── repository/          # Data access
│   └── service/             # User business logic
│
├── file/                    # File management module
│   ├── controller/          # File endpoints
│   ├── dto/                 # File DTOs
│   ├── entity/              # File metadata entity
│   ├── repository/          # File data access
│   └── service/             # File storage logic
│
├── email/                   # Email service module
│   └── service/             # Email sending logic
│
├── security/                # Security components
│   ├── JwtService           # JWT token management
│   ├── JwtAuthenticationFilter
│   └── JwtAuthenticationEntryPoint
│
├── config/                  # Configuration classes
│   ├── SecurityConfig       # Security setup
│   ├── OpenApiConfig        # Swagger config
│   ├── CorsConfig           # CORS setup
│   └── AsyncConfig          # Async processing
│
└── common/                  # Shared components
    ├── dto/                 # Common DTOs
    │   ├── ApiResponse      # Standard response wrapper
    │   └── PageResponse     # Pagination wrapper
    ├── entity/              # Base entities
    │   └── BaseEntity       # Auditing fields
    ├── exception/           # Custom exceptions
    │   ├── GlobalExceptionHandler
    │   ├── ResourceNotFoundException
    │   └── BadRequestException
    └── filter/              # Request filters
        └── RequestLoggingFilter
```

## Design Patterns

### 1. **Layered Architecture**
- **Controller Layer**: Handles HTTP requests/responses
- **Service Layer**: Contains business logic
- **Repository Layer**: Data access abstraction
- **Entity Layer**: Domain models

### 2. **DTO Pattern**
- Separates internal entities from API contracts
- Prevents over-exposure of domain models
- Enables API versioning

### 3. **Repository Pattern**
- Abstracts data access logic
- Uses Spring Data JPA
- Enables easy testing with mocks

### 4. **Mapper Pattern**
- Converts between entities and DTOs
- Centralizes transformation logic
- Keeps layers decoupled

### 5. **Builder Pattern**
- Used with Lombok `@Builder`
- Provides fluent object creation
- Improves code readability

## Security Architecture

### JWT Authentication Flow

```
1. User Login
   ↓
2. Validate Credentials
   ↓
3. Generate JWT Token
   ↓
4. Return Token to Client
   ↓
5. Client Includes Token in Header
   ↓
6. JwtAuthenticationFilter Validates Token
   ↓
7. Set Authentication in SecurityContext
   ↓
8. Access Protected Resource
```

### Security Components

- **JwtService**: Token generation and validation
- **JwtAuthenticationFilter**: Intercepts requests, validates tokens
- **JwtAuthenticationEntryPoint**: Handles authentication errors
- **SecurityConfig**: Configures security rules
- **UserDetailsService**: Loads user for authentication

## Database Schema

### Users Table
```sql
users (
  id BIGSERIAL PRIMARY KEY,
  first_name VARCHAR(255),
  last_name VARCHAR(255),
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  role VARCHAR(50),
  enabled BOOLEAN,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)
```

### File Metadata Table
```sql
file_metadata (
  id BIGSERIAL PRIMARY KEY,
  file_name VARCHAR(255) UNIQUE,
  original_file_name VARCHAR(255),
  content_type VARCHAR(255),
  file_size BIGINT,
  file_path VARCHAR(500),
  uploaded_by BIGINT FK -> users(id),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
)
```

## API Response Format

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... },
  "timestamp": "2024-05-24T10:30:00"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error message",
  "errors": { ... },
  "timestamp": "2024-05-24T10:30:00"
}
```

## Configuration Management

### Profile-Based Configuration
- **dev**: Development settings (verbose logging, H2 console)
- **test**: Test settings (in-memory database)
- **prod**: Production settings (optimized, secure)

### Environment Variables
All sensitive configuration externalized via environment variables:
- Database credentials
- JWT secrets
- Email credentials
- CORS origins

## Logging Strategy

### Log Levels
- **ERROR**: System errors, exceptions
- **WARN**: Warnings, deprecated usage
- **INFO**: Important business events
- **DEBUG**: Detailed debugging information

### Structured Logging
- Request/Response logging via filter
- Method execution logging in services
- Security events logging
- Database query logging (dev only)

## Testing Strategy

### Unit Tests
- Test individual components in isolation
- Mock dependencies
- Focus on business logic

### Integration Tests
- Test complete request/response flow
- Use test database
- Verify security rules

### Test Coverage
- Services: Business logic coverage
- Controllers: API contract validation
- Security: Authentication/authorization

## Scalability Considerations

### Horizontal Scaling
- Stateless design (JWT tokens)
- No server-side sessions
- Database connection pooling

### Performance
- Lazy loading for JPA entities
- Pagination for large datasets
- Async email processing
- Database indexing

### Caching (Future Enhancement)
- Redis for session management
- Cache frequently accessed data
- Distributed caching support

## Security Best Practices

1. **Password Security**: BCrypt hashing
2. **JWT Security**: Signed tokens, expiration
3. **Input Validation**: Jakarta Validation
4. **SQL Injection Prevention**: JPA/Hibernate
5. **CORS Configuration**: Restricted origins
6. **HTTPS**: Recommended for production
7. **Rate Limiting**: (Future enhancement)

## Monitoring & Observability

### Spring Boot Actuator
- `/actuator/health`: Application health
- `/actuator/info`: Application info
- `/actuator/metrics`: Performance metrics

### Logging
- Structured logs for analysis
- Log aggregation ready
- Request tracing

## Extension Points

### Adding New Modules

1. Create package: `com.starter.yourmodule`
2. Define entity extending `BaseEntity`
3. Create repository interface
4. Implement service with business logic
5. Add DTOs and mapper
6. Create controller with endpoints
7. Add Flyway migration
8. Write tests

### Adding New Features

- **Caching**: Add Spring Cache + Redis
- **Message Queue**: Add RabbitMQ/Kafka
- **Search**: Add Elasticsearch
- **API Versioning**: URL or header-based
- **Rate Limiting**: Add Bucket4j
- **Audit Logging**: Extend BaseEntity

## Dependencies

### Core
- Spring Boot 3.2.5
- Spring Security
- Spring Data JPA
- PostgreSQL Driver

### Utilities
- Lombok
- Flyway
- JJWT

### Documentation
- SpringDoc OpenAPI

### Testing
- JUnit 5
- Mockito
- Spring Boot Test

## Deployment

### Docker
- Multi-stage build for optimization
- Alpine-based images for size
- Docker Compose for local development

### Production Checklist
- [ ] Update JWT secret
- [ ] Configure production database
- [ ] Set up HTTPS/SSL
- [ ] Configure email service
- [ ] Set up monitoring
- [ ] Configure log aggregation
- [ ] Set up backup strategy
- [ ] Configure rate limiting
- [ ] Review security settings
- [ ] Set up CI/CD pipeline
