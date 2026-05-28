# Spring Boot Starter

A production-ready, flexible, and reusable Spring Boot backend starter project following clean architecture and industry best practices.

## Features

### Core Architecture
- **Layered Architecture**: Controller → Service → Repository → Entity
- **SOLID Principles**: Clean, maintainable, and extensible code
- **Feature-based Package Structure**: Organized by domain modules

### Backend Features
- RESTful API design
- Global exception handling with `@ControllerAdvice`
- Input validation using Jakarta Validation
- Standard API response format (success/error wrapper)
- Pagination and sorting support

### Database
- Spring Data JPA
- PostgreSQL (configurable via profiles)
- UUID primary keys for all entities
- Flyway migrations
- Auditing fields (createdAt, updatedAt)

### Security
- Spring Security
- JWT authentication (login/register/refresh token)
- Role-based authorization (USER, ADMIN)
- BCrypt password encryption

### Configuration
- Multiple profiles: `dev`, `test`, `prod`
- Externalized configuration via environment variables
- CORS configuration

### Logging & Monitoring
- SLF4J + Logback
- Structured logging
- Spring Boot Actuator endpoints

### API Documentation
- Swagger/OpenAPI 3.0
- Interactive API documentation at `/swagger-ui.html`

### DevOps
- Dockerfile for containerization
- Docker Compose (app + database)
- Maven build lifecycle
- **Spring Boot DevTools**: Automatic server restart on code changes

### Code Quality
- Lombok for boilerplate reduction
- Clean code structure

## Tech Stack

- **Java**: 17
- **Spring Boot**: 3.2.5
- **Database**: PostgreSQL 16
- **Migration**: Flyway
- **Security**: Spring Security + JWT
- **Documentation**: SpringDoc OpenAPI
- **Build Tool**: Maven
- **Containerization**: Docker

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 16 (or use Docker Compose)
- Docker & Docker Compose (optional)

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd spring-boot-starter
```

### 2. Configure Database

Update `src/main/resources/application.yml` or set environment variables:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/starter_db
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=postgres
```

### 3. Run with Maven

```bash
# Build the project
mvn clean install

# Run the application (with auto-restart enabled in dev profile)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will start on `http://localhost:8080`

**Note**: When running in development mode, Spring Boot DevTools will automatically restart the server whenever you make changes to your Java code. Simply save your files and the application will reload within seconds.

### 4. Run with Docker Compose

```bash
docker-compose up --build
```

This will start both PostgreSQL and the application.

## API Documentation

Once the application is running, access the interactive Swagger UI:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs
- **OpenAPI YAML**: http://localhost:8080/api-docs.yaml

The Swagger UI provides:
- Interactive API testing directly from your browser
- Detailed request/response schemas with examples
- Built-in JWT authentication support
- Comprehensive endpoint documentation

The Swagger UI provides comprehensive documentation for all API endpoints.

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/login` | Login user |
| POST | `/api/v1/auth/refresh` | Refresh access token |

### Users

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/users/{id}` | Get user by ID | Required |
| GET | `/api/v1/users` | Get all users (paginated) | Admin only |
| DELETE | `/api/v1/users/{id}` | Delete user | Admin only |

### Email

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/v1/emails/send` | Send custom email | Admin only |
| POST | `/api/v1/emails/welcome` | Send welcome email | Admin only |
| POST | `/api/v1/emails/test` | Send test email | Admin only |

## Configuration Profiles

### Development (`dev`)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Test (`test`)
```bash
mvn test
```

### Production (`prod`)
```bash
java -jar target/spring-boot-starter-1.0.0.jar --spring.profiles.active=prod
```

## Email Configuration

The application includes email functionality for sending notifications and welcome messages. To enable email sending:

### Gmail Configuration (Recommended for Development)

1. **Enable 2-Factor Authentication** on your Gmail account
2. **Generate an App Password**:
   - Go to Google Account Settings → Security
   - Under "2-Step Verification", select "App passwords"
   - Generate a new app password for "Mail"
3. **Set Environment Variables**:
   ```bash
   export MAIL_HOST=smtp.gmail.com
   export MAIL_PORT=587
   export MAIL_USERNAME=your-email@gmail.com
   export MAIL_PASSWORD=your-16-char-app-password
   ```

### Other SMTP Providers

For other email providers (SendGrid, Mailgun, AWS SES, etc.), update the configuration:

```bash
export MAIL_HOST=smtp.your-provider.com
export MAIL_PORT=587
export MAIL_USERNAME=your-username
export MAIL_PASSWORD=your-password
```

### Testing Email Configuration

Use the test endpoint to verify your email setup:

```bash
# Login as admin
TOKEN=$(curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"Admin@123"}' \
  | jq -r '.data.accessToken')

# Send test email
curl -X POST "http://localhost:8080/api/v1/emails/test?email=your-email@example.com" \
  -H "Authorization: Bearer $TOKEN"
```

**Note**: Email sending is asynchronous and runs in the background. Check your application logs for email delivery status.

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILE` | Active profile | `dev` |
| `DATABASE_URL` | Database connection URL | `jdbc:postgresql://localhost:5432/starter_db` |
| `DATABASE_USERNAME` | Database username | `postgres` |
| `DATABASE_PASSWORD` | Database password | `postgres` |
| `JWT_SECRET` | JWT secret key | (default provided) |
| `JWT_EXPIRATION` | Access token expiration (ms) | `86400000` (24h) |
| `JWT_REFRESH_EXPIRATION` | Refresh token expiration (ms) | `604800000` (7d) |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | `http://localhost:3000,http://localhost:4200` |
| `MAIL_HOST` | SMTP server host | `smtp.gmail.com` |
| `MAIL_PORT` | SMTP server port | `587` |
| `MAIL_USERNAME` | Email account username | (required for sending emails) |
| `MAIL_PASSWORD` | Email account password | (required for sending emails) |

## Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Project Structure

```
src/
├── main/
│   ├── java/com/starter/
│   │   ├── auth/              # Authentication module
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   └── service/
│   │   ├── common/            # Shared components
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   └── exception/
│   │   ├── config/            # Configuration classes
│   │   ├── security/          # Security components
│   │   ├── user/              # User module
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── mapper/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── StarterApplication.java
│   └── resources/
│       ├── db/migration/      # Flyway migrations
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-test.yml
│       └── application-prod.yml
└── test/                      # Test files
```

## Sample Usage

### Register a User

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

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Access Protected Endpoint

```bash
curl -X GET http://localhost:8080/api/v1/users/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer <your-jwt-token>"
```

### Send Test Email (Admin Only)

```bash
# First, login as admin to get the JWT token
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "Admin@123"
  }'

# Then send a test email
curl -X POST "http://localhost:8080/api/v1/emails/test?email=recipient@example.com" \
  -H "Authorization: Bearer <admin-jwt-token>"
```

### Send Custom Email (Admin Only)

```bash
curl -X POST http://localhost:8080/api/v1/emails/send \
  -H "Authorization: Bearer <admin-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "user@example.com",
    "subject": "Welcome!",
    "message": "Thank you for joining us!",
    "html": false
  }'
```

### Send Welcome Email (Admin Only)

```bash
curl -X POST http://localhost:8080/api/v1/emails/welcome \
  -H "Authorization: Bearer <admin-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "newuser@example.com",
    "userName": "John Doe"
  }'
```

## Extending the Application

### Adding a New Module

1. Create package structure: `com.starter.yourmodule`
2. Add entity extending `BaseEntity`
3. Create repository extending `JpaRepository`
4. Implement service layer
5. Add DTOs and mappers
6. Create controller with proper annotations
7. Add Flyway migration if needed

### Example Module Structure

```
yourmodule/
├── controller/
│   └── YourController.java
├── dto/
│   ├── YourRequest.java
│   └── YourResponse.java
├── entity/
│   └── YourEntity.java
├── mapper/
│   └── YourMapper.java
├── repository/
│   └── YourRepository.java
└── service/
    └── YourService.java
```

## Monitoring

Access actuator endpoints:

- Health: http://localhost:8080/actuator/health
- Info: http://localhost:8080/actuator/info
- Metrics: http://localhost:8080/actuator/metrics

## Development Features

### Auto-Restart with DevTools

Spring Boot DevTools is configured for the development profile and provides:

- **Automatic Restart**: The server automatically restarts when you modify Java classes, resources, or configuration files
- **LiveReload**: Browser auto-refresh support (requires LiveReload browser extension)
- **Fast Restart**: Uses two classloaders for quick restart times

**How it works**:
1. Run the application with the `dev` profile: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
2. Make changes to your Java code
3. Save the file (or build the project)
4. DevTools detects the change and automatically restarts the application

**IDE Configuration**:
- **IntelliJ IDEA**: Enable "Build project automatically" in Settings → Build, Execution, Deployment → Compiler
- **Eclipse**: Auto-build is enabled by default
- **VS Code**: Save files to trigger rebuild

**What triggers a restart**:
- Changes to Java source files
- Changes to resources (application.yml, etc.)
- Changes to dependencies in pom.xml

**What doesn't trigger a restart**:
- Static resources (excluded by default)
- View templates in `/static` or `/public`

## License

This project is licensed under the Apache License 2.0.

## Support

For issues and questions, please open an issue in the repository.
