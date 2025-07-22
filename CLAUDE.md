# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Chemist-BE is a Spring Boot 3.4.7 REST API application for managing educational data including students, teachers, schedules, grades, and academic operations. It uses Java 21, PostgreSQL database, JWT authentication, and follows a layered architecture pattern.

## Build and Development Commands

### Core Maven Commands
- `./mvnw clean compile` - Compile the project and generate MapStruct mappers
- `./mvnw test` - Run all unit and integration tests
- `./mvnw spring-boot:run` - Start the application on localhost:8080
- `./mvnw clean package` - Build JAR file for deployment

### Database Setup
- Requires PostgreSQL database named `chemist` running on localhost:5432
- Default credentials: username=postgres, password=root (configurable via environment variables)
- Uses environment variables: `DB_USERNAME` and `DB_PASSWORD`

### Development Profiles
- `dev` - Development profile (default)
- `test` - Testing profile with H2 in-memory database
- Configure via `spring.profiles.active` in application.properties

## Architecture and Code Patterns

### Layered Architecture
Follow this strict 6-layer pattern for all entities:
1. **Entity** (`model/`) - JPA entities extending BaseEntity with soft delete support
2. **DTO** (`dto/`) - Data transfer objects with `@JsonProperty` snake_case mapping  
3. **Mapper** (`mapper/`) - MapStruct mappers with `componentModel = "spring"`
4. **Repository** (`repository/`) - JPA repositories with soft delete queries
5. **Service** (`service/`) - Business logic with `@Transactional` support
6. **Controller** (`controller/`) - REST endpoints with `/api/v1/` prefix

### Key Technologies
- **Blaze Persistence** - Advanced JPA queries and entity views (configured in BlazePersistenceConfiguration)
- **MapStruct** - Automatic DTO mapping with annotation processors
- **Spring Security** - JWT-based authentication with custom UserDetailsService
- **Lombok** - Automatic getters/setters (ensure proper annotation processor setup)
- **SpringDoc OpenAPI** - API documentation at `/swagger-ui.html`

### Naming Conventions
- **Database**: snake_case for tables/columns (e.g., `first_name`, `created_at`)
- **Java**: camelCase for fields/methods (e.g., `firstName`, `createdAt`)
- **JSON**: snake_case for API requests/responses via `@JsonProperty`
- **URLs**: kebab-case for multi-word endpoints (e.g., `/academic-year`)

### Soft Delete Implementation
All entities must implement soft delete:
- Add `Boolean deleted = false` field to entities
- Create `findActiveById()` and `findAllActive()` repository methods
- Filter deleted records in all queries

### Authentication and Security
- JWT tokens required for most endpoints
- Role-based access control (ADMIN, TEACHER, MANAGER roles)
- Custom authentication filter in SecurityConfig
- Some endpoints require specific roles (documented in INSTRUCTION.md)

## Important Files and Configurations

### Configuration Files
- `SecurityConfig.java` - JWT and role-based security setup
- `BlazePersistenceConfiguration.java` - Advanced query capabilities configuration
- `application.properties` - Database and timezone settings (Asia/Ho_Chi_Minh)

### Core Entity Relationships
- Students belong to Groups
- Groups have Schedules with Teachers and Rooms
- Attendance tracking linked to Schedules and Students
- Academic Years organize Groups by time periods
- Scores link Students to Exams

### API Documentation
- Comprehensive API docs in `/docs/INSTRUCTION.md`
- All endpoints follow `/api/v1/` prefix pattern
- Supports pagination, filtering, and search operations
- Swagger UI available at runtime for interactive testing

## Development Guidelines

### When Creating New Features
1. Read existing similar implementations first
2. Follow the exact 6-layer pattern (Entity → DTO → Mapper → Repository → Service → Controller)
3. Implement soft delete in entities and repositories
4. Use proper annotations (`@Entity`, `@Service`, `@RestController`, etc.)
5. Test with `./mvnw test` and run application to verify

### Code Quality Requirements
- All entities must extend `BaseEntity` for audit fields
- DTOs must use `@JsonProperty` for snake_case JSON mapping
- Repositories must include soft delete query methods
- Services must be `@Transactional` for data operations
- Follow the annotation processor configuration for MapStruct + Lombok integration

### Testing Approach
- Unit tests for service layer business logic
- Integration tests for controller endpoints
- Repository tests for custom query methods
- Use H2 in-memory database for testing (already configured)