# Agent Instructions for Chemist-BE Project

This document provides detailed instructions for AI agents working on the Chemist-BE project, based on Junie guidelines and project conventions.

## Project Overview

The Chemist-BE is a Java Spring Boot application built with Maven, following a standard layered architecture. All development must adhere to the established patterns and conventions outlined below.

## Development Workflow

### 1. Before Making Changes
- Always read existing code to understand current patterns
- Check for similar implementations in the codebase
- Validate that changes align with existing architecture
- Use `get_errors` tool after making changes to ensure code compiles

### 2. Required Tools Usage
- Use `read_file` to understand existing implementations
- Use `insert_edit_into_file` for all code modifications
- Use `get_errors` to validate changes
- Use `file_search` or `grep_search` to find related code

## Entity Development Pattern

When creating or modifying entities, follow this strict pattern:

### Step 1: Entity Class (in `model` package)
```java
@Entity
@Table(name = "table_name")
public class EntityName extends BaseEntity {
    // Fields with proper JPA annotations
    @Column(name = "field_name")
    private String fieldName;
    
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;
    
    // Constructors, getters, setters
}
```

**Requirements:**
- Extend `BaseEntity` for common fields (id, createdAt, updatedAt)
- Use `@Entity` and `@Table(name = "table_name")` annotations
- Implement soft delete with `deleted` boolean field
- Use snake_case for database column names
- Use camelCase for Java field names

### Step 2: DTO Class (in `dto` package)
```java
public class EntityNameDto {
    @JsonProperty("field_name")
    private String fieldName;
    
    // Constructors, getters, setters
}
```

**Requirements:**
- Use `@JsonProperty("snake_case_name")` for all fields
- Field names in Java should be camelCase
- JSON serialization should use snake_case

### Step 3: Mapper Class (in `mapper` package)
```java
@Mapper(componentModel = "spring", uses = {OtherMapper.class})
public abstract class EntityNameMapper {
    
    public abstract EntityNameDto toDto(EntityName entity);
    public abstract EntityName toEntity(EntityNameDto dto);
    
    // Custom mapping methods if needed
}
```

**Requirements:**
- Use `@Mapper(componentModel = "spring")` annotation
- Name as `EntityNameMapper`
- Include `uses = {OtherMapper.class}` for nested object mapping
- Use abstract class for complex mappings, interface for simple ones
- Provide `toDto()` and `toEntity()` methods

### Step 4: Repository Interface (in `repository` package)
```java
@Repository
public interface EntityNameRepository extends JpaRepository<EntityName, Long> {
    
    @Query("SELECT e FROM EntityName e WHERE e.deleted = false AND e.id = :id")
    Optional<EntityName> findActiveById(@Param("id") Long id);
    
    @Query("SELECT e FROM EntityName e WHERE e.deleted = false")
    List<EntityName> findAllActive();
}
```

**Requirements:**
- Extend `JpaRepository<Entity, Long>`
- Use `@Repository` annotation
- Implement soft delete queries (`findActiveById`, `findAllActive`)
- Use `@Query` for custom queries when needed

### Step 5: Service Class (in `service` package)
```java
@Service
@Transactional
public class EntityNameService {
    
    private final EntityNameRepository repository;
    private final EntityNameMapper mapper;
    
    // Constructor injection
    // Business logic methods
}
```

### Step 6: Controller Class (in `controller` package)
```java
@RestController
@RequestMapping("/api/entity-name")
@Validated
public class EntityNameController {
    
    private final EntityNameService service;
    
    // Constructor injection
    // REST endpoints
}
```

## Code Standards

### Naming Conventions
- **Classes**: PascalCase (e.g., `UserService`, `ScheduleController`)
- **Methods**: camelCase (e.g., `findActiveById`, `createSchedule`)
- **Variables**: camelCase (e.g., `userName`, `scheduleDto`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_RETRY_ATTEMPTS`)
- **Database tables**: snake_case (e.g., `user_profile`, `schedule_item`)
- **Database columns**: snake_case (e.g., `created_at`, `user_name`)

### Annotation Usage
- **Entities**: `@Entity`, `@Table`, `@Column`, `@Id`, `@GeneratedValue`
- **DTOs**: `@JsonProperty` for field mapping
- **Mappers**: `@Mapper(componentModel = "spring")`
- **Repositories**: `@Repository`
- **Services**: `@Service`, `@Transactional`
- **Controllers**: `@RestController`, `@RequestMapping`, `@Validated`

### Builder Pattern Implementation
When implementing builder pattern (e.g., for Schedule):

```java
public class ScheduleBuilder {
    private Schedule schedule;
    
    public ScheduleBuilder() {
        this.schedule = new Schedule();
    }
    
    public ScheduleBuilder withField(Type value) {
        this.schedule.setField(value);
        return this;
    }
    
    public Schedule build() {
        return this.schedule;
    }
}
```

## File Organization Rules

### Package Structure
```
dthaibinhf.project.chemistbe/
├── config/          # Spring configuration classes
├── constants/       # Application-wide constants
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── exception/      # Custom exceptions and handlers
├── filter/         # Request/response filters
├── mapper/         # MapStruct mappers
├── model/          # JPA entities
├── repository/     # Spring Data JPA repositories
└── service/        # Business logic services
```

### File Naming
- **Entities**: `EntityName.java` (e.g., `User.java`, `Schedule.java`)
- **DTOs**: `EntityNameDto.java` (e.g., `UserDto.java`, `ScheduleDto.java`)
- **Mappers**: `EntityNameMapper.java` (e.g., `UserMapper.java`)
- **Repositories**: `EntityNameRepository.java`
- **Services**: `EntityNameService.java`
- **Controllers**: `EntityNameController.java`

## Error Handling

### Always Use Try-Catch for:
- Database operations
- External API calls
- File operations
- Data transformation

### Custom Exceptions
- Create specific exceptions in `exception` package
- Use `@ControllerAdvice` for global exception handling
- Return appropriate HTTP status codes

## Testing Requirements

### Unit Tests
- Test all service methods
- Test repository queries
- Test mapper conversions
- Mock dependencies using `@MockBean`

### Integration Tests
- Test controller endpoints
- Test database interactions
- Use `@SpringBootTest` for full context

## Documentation Standards

### Code Comments
- Document complex business logic
- Explain non-obvious implementations
- Use JavaDoc for public methods

### API Documentation
- Use Swagger/OpenAPI annotations
- Document all endpoints
- Provide example requests/responses

## Security Considerations

### Data Access
- Always use soft delete queries in repositories
- Implement proper authorization checks
- Validate input data

### Database
- Use parameterized queries
- Implement proper indexing
- Follow database naming conventions

## Performance Guidelines

### Database Optimization
- Use appropriate fetch strategies
- Implement pagination for large datasets
- Optimize query performance

### Caching
- Use Spring Cache where appropriate
- Cache frequently accessed data
- Implement cache eviction strategies

## Agent-Specific Instructions

### When Creating New Features
1. Analyze existing similar implementations
2. Follow the exact pattern outlined above
3. Create all required files (Entity, DTO, Mapper, Repository, Service, Controller)
4. Ensure proper imports and dependencies
5. Run error checks after each file creation
6. Create corresponding tests

### When Modifying Existing Code
1. Read the current implementation first
2. Understand the existing pattern
3. Make minimal changes that maintain consistency
4. Update related files (DTOs, Mappers) as needed
5. Validate changes don't break existing functionality

### When Debugging
1. Check for compilation errors first
2. Verify all required annotations are present
3. Ensure proper dependency injection
4. Check database schema alignment
5. Validate mapper configurations

## Common Pitfalls to Avoid

1. **Don't** create entities without extending `BaseEntity`
2. **Don't** forget to implement soft delete functionality
3. **Don't** mix camelCase and snake_case incorrectly
4. **Don't** create repositories without `@Repository` annotation
5. **Don't** forget to use `@JsonProperty` in DTOs
6. **Don't** create mappers without `componentModel = "spring"`
7. **Don't** skip error validation after making changes

## Tools and Commands

### Required Maven Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- MapStruct
- Validation API
- Database drivers (PostgreSQL/MySQL)

### Build and Test Commands
```bash
mvn clean compile          # Compile the project
mvn test                  # Run tests
mvn spring-boot:run       # Run the application
```

Remember: Always follow these guidelines strictly to maintain code consistency and project integrity.
