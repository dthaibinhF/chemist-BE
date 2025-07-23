# Complete Pagination Guide for Chemist-BE Spring Boot Project

## Table of Contents
1. [What is Pagination?](#what-is-pagination)
2. [Libraries and Dependencies](#libraries-and-dependencies)
3. [Why We Use Spring Data Pagination](#why-we-use-spring-data-pagination)
4. [Complete Implementation Flow](#complete-implementation-flow)
5. [Controller Layer Implementation](#controller-layer-implementation)
6. [Service Layer Implementation](#service-layer-implementation)
7. [Repository Layer Implementation](#repository-layer-implementation)
8. [Best Practices and Examples](#best-practices-and-examples)
9. [Troubleshooting Common Issues](#troubleshooting-common-issues)

---

## What is Pagination?

**Pagination** is a technique used to divide large datasets into smaller, manageable chunks or "pages". Instead of loading thousands of records at once (which would be slow and memory-intensive), pagination allows you to:

- Load only a specific number of records per request (e.g., 20 students per page)
- Navigate through different pages of data (page 1, 2, 3, etc.)
- Sort data by different fields (name, date, id, etc.)
- Provide total count information to users

### Example:
Instead of loading 1000 students at once:
```
Page 1: Students 1-20    (size=20, page=0)
Page 2: Students 21-40   (size=20, page=1)  
Page 3: Students 41-60   (size=20, page=2)
...
```

---

## Libraries and Dependencies

### Primary Library: Spring Data JPA
Your project uses **Spring Data JPA** which provides built-in pagination support.

### Key Classes Used:
```java
import org.springframework.data.domain.Page;        // Contains paginated results + metadata
import org.springframework.data.domain.PageRequest; // Creates pagination requests
import org.springframework.data.domain.Pageable;    // Interface for pagination parameters
import org.springframework.data.domain.Sort;        // Handles sorting
import org.springframework.data.web.PageableDefault; // Default pagination settings
```

### Maven Dependencies (Already in your project):
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

---

## Why We Use Spring Data Pagination

### 1. **Database Efficiency**
- Uses `LIMIT` and `OFFSET` in SQL queries
- Reduces memory usage by loading only needed records
- Faster query execution for large datasets

### 2. **Built-in Metadata**
- Total elements count
- Total pages
- Current page information
- Whether there are next/previous pages

### 3. **Automatic Integration**
- Works seamlessly with Spring MVC
- Automatic parameter binding from HTTP requests
- JSON serialization support

### 4. **Sorting Support**
- Multi-field sorting
- Ascending/descending order
- Dynamic sorting based on user input

---

## Complete Implementation Flow

```
HTTP Request → Controller → Service → Repository → Database
     ↓             ↓          ↓          ↓           ↓
   ?page=0    Pageable    Page<Entity> Page<Entity> LIMIT/OFFSET
   &size=20   Parameters   Processing    Query       SQL Query
   &sort=name,asc  ↓          ↓          ↓           ↓
                   ↓      Page<DTO>  Page<Entity>   Results
                   ↓       Response      ↑           ↑
                   ←---------←-----------←-----------←
```

---

## Controller Layer Implementation

### Method 1: Manual Parameter Handling (Recommended)

```java
@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    
    @GetMapping("/search")
    public ResponseEntity<Page<StudentDTO>> searchStudents(
        // Search criteria parameters
        @RequestParam(name = "studentId", required = false) Integer studentId,
        @RequestParam(name = "phone", required = false) String phone,
        @RequestParam(name = "name", required = false) String name,
        
        // Pagination parameters with default values
        @RequestParam(defaultValue = "0", name = "page") int page,           // Page number (0-based)
        @RequestParam(name = "size", defaultValue = "20") int size,         // Items per page
        @RequestParam(name = "sort", defaultValue = "id,asc") String sort   // Sort field and direction
    ) {
        // Parse sorting parameter manually
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];                                   // Field to sort by
        Sort.Direction direction = sortParams.length > 1 && 
            "desc".equalsIgnoreCase(sortParams[1]) ? 
            Sort.Direction.DESC : Sort.Direction.ASC;                       // Sort direction
        
        // Create Pageable object
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        
        // Call service method
        Page<StudentDTO> studentsPage = studentService.searchStudents(
                studentId, phone, name, pageable);
                
        return ResponseEntity.ok(studentsPage);
    }
}
```

#### Key Variables and Annotations Explained:

| Parameter | Type | Purpose | Default | Required |
|-----------|------|---------|---------|----------|
| `page` | `int` | Page number (0-based) | 0 | No |
| `size` | `int` | Items per page | 20 | No |
| `sort` | `String` | Sort field and direction | "id,asc" | No |
| `@RequestParam` | Annotation | Maps HTTP parameters to method parameters | - | Yes |
| `defaultValue` | Attribute | Provides default if parameter is missing | - | No |
| `required = false` | Attribute | Makes parameter optional | true | No |

#### Example HTTP Requests:
```bash
# Basic pagination
GET /api/v1/students/search?page=0&size=10

# With sorting
GET /api/v1/students/search?page=1&size=15&sort=name,desc

# With search criteria
GET /api/v1/students/search?name=John&phone=123&page=0&size=20&sort=createdAt,asc
```

### Method 2: @PageableDefault Approach (Simpler but Less Flexible)

```java
@GetMapping("/list")
public ResponseEntity<List<StudentDTO>> getStudents(
    @PageableDefault(
        size = 20,                          // Default page size
        page = 0,                          // Default page number
        sort = "id",                       // Default sort field
        direction = Sort.Direction.ASC      // Default sort direction
    ) Pageable pageable
) {
    List<StudentDTO> students = studentService.getAllStudents(pageable);
    return ResponseEntity.ok(students);
}
```

#### @PageableDefault Attributes:
- `size`: Default number of items per page
- `page`: Default page number (0-based)
- `sort`: Default field to sort by
- `direction`: Default sort direction (ASC/DESC)

#### Example HTTP Requests:
```bash
# Uses defaults (page=0, size=20, sort=id,asc)
GET /api/v1/students/list

# Override defaults
GET /api/v1/students/list?page=1&size=10&sort=name,desc
```

---

## Service Layer Implementation

### Purpose:
- Process pagination parameters
- Handle business logic
- Convert entities to DTOs while preserving pagination metadata

```java
@Service
@Transactional
@Slf4j
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;  // MapStruct mapper
    
    @Transactional(readOnly = true)  // Optimize for read operations
    public Page<StudentDTO> searchStudents(Integer studentId, String phone, String name, 
                                         Pageable pageable) {
        try {
            // Log pagination request for debugging
            log.info("Searching students - page: {}, size: {}, sort: {}", 
                    pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
            
            // Prepare search patterns for LIKE queries
            String phonePattern = phone != null ? "%" + phone + "%" : null;
            String namePattern = name != null ? "%" + name + "%" : null;
            
            // Call repository method
            Page<Student> studentsPage = studentRepository.searchStudents(
                    studentId, phonePattern, namePattern, pageable);
            
            // Log results
            log.info("Found {} total students, {} pages", 
                    studentsPage.getTotalElements(), studentsPage.getTotalPages());
            
            // Convert Page<Entity> to Page<DTO> using map()
            return studentsPage.map(studentMapper::toDto);
            
        } catch (Exception e) {
            log.error("Error searching students with pagination", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Failed to search students");
        }
    }
}
```

#### Key Service Layer Concepts:

1. **Page.map() Method**:
   ```java
   // Converts Page<Student> to Page<StudentDTO>
   return studentsPage.map(studentMapper::toDto);
   ```
   - Preserves all pagination metadata (total pages, total elements, etc.)
   - Transforms only the content using the mapper
   - Returns same Page structure with different content type

2. **Pattern Preparation**:
   ```java
   String namePattern = name != null ? "%" + name + "%" : null;
   ```
   - Adds SQL wildcards for LIKE queries
   - Handles null values gracefully

3. **Transaction Management**:
   - `@Transactional(readOnly = true)`: Optimizes read operations
   - Reduces database locking overhead

---

## Repository Layer Implementation

### JPA Repository with Pagination Support

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    
    // Method 1: Native SQL Query with Pagination
    @Query(value = """
         SELECT DISTINCT s.* FROM student s 
         LEFT JOIN student_detail sd ON s.id = sd.student_id 
         LEFT JOIN "group" g ON sd.group_id = g.id 
         LEFT JOIN school sc ON sd.school_id = sc.id 
         WHERE s.end_at IS NULL                                    -- Soft delete filter
         AND (:studentId IS NULL OR s.id = :studentId)            -- Optional ID filter
         AND (:phone IS NULL OR s.parent_phone LIKE :phone)       -- Optional phone search
         AND (:name IS NULL OR LOWER(s.name) LIKE LOWER(:name))   -- Case-insensitive name search
         AND (:groupName IS NULL OR LOWER(g.name::text) LIKE LOWER(:groupName))
        """, nativeQuery = true)
    Page<Student> searchStudents(@Param("studentId") Integer studentId,
                                 @Param("phone") String phone,
                                 @Param("name") String name,
                                 @Param("groupName") String groupName,
                                 Pageable pageable);  // ← This enables pagination
    
    // Method 2: JPQL Query with Pagination
    @Query("SELECT s FROM Student s WHERE s.endAt IS NULL AND " +
           "(:name IS NULL OR LOWER(s.name) LIKE LOWER(:name))")
    Page<Student> findActiveStudentsByName(@Param("name") String name, Pageable pageable);
    
    // Method 3: Query Method with Pagination (Spring Data auto-generation)
    Page<Student> findByEndAtIsNullAndNameContainingIgnoreCase(String name, Pageable pageable);
    
    // Method 4: Simple findAll with pagination
    @Query("SELECT s FROM Student s WHERE s.endAt IS NULL")
    Page<Student> findAllActive(Pageable pageable);
}
```

#### Key Repository Concepts:

1. **Pageable Parameter**:
   - Always last parameter in method signature
   - Automatically handled by Spring Data
   - Contains page, size, and sort information

2. **Return Type - Page<T>**:
   ```java
   Page<Student> // Returns full pagination metadata
   List<Student> // Returns only content (loses pagination info)
   ```

3. **Native vs JPQL**:
   - **Native SQL**: Use when you need complex joins or database-specific features
   - **JPQL**: Use for simpler queries, more database-agnostic

4. **Parameter Handling**:
   ```sql
   (:name IS NULL OR LOWER(s.name) LIKE LOWER(:name))
   ```
   - Handles optional search parameters
   - Performs case-insensitive searches

5. **Soft Delete Integration**:
   ```sql
   WHERE s.end_at IS NULL  -- Only active records
   ```

#### Automatic SQL Generation:
When you pass a `Pageable` parameter, Spring Data automatically adds:
```sql
-- Your query becomes:
SELECT DISTINCT s.* FROM student s WHERE ... 
LIMIT 20 OFFSET 0    -- Added automatically based on Pageable
ORDER BY s.id ASC    -- Added automatically based on Sort
```

---

## Best Practices and Examples

### 1. Pagination Response Structure

When you return `Page<StudentDTO>`, the JSON response includes:

```json
{
  "content": [
    {
      "id": 1,
      "name": "John Doe",
      "phone": "123-456-7890"
    },
    {
      "id": 2,
      "name": "Jane Smith", 
      "phone": "098-765-4321"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 20,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 150,
  "totalPages": 8,
  "last": false,
  "first": true,
  "numberOfElements": 20,
  "size": 20,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "empty": false
}
```

### 2. Default Values Strategy

```java
// Recommended defaults for your project
@RequestParam(defaultValue = "0", name = "page") int page,        // Start from first page
@RequestParam(name = "size", defaultValue = "20") int size,       // Reasonable page size
@RequestParam(name = "sort", defaultValue = "id,asc") String sort // Consistent default sort
```

### 3. Maximum Page Size Protection

```java
@GetMapping("/search")
public ResponseEntity<Page<StudentDTO>> searchStudents(
    @RequestParam(defaultValue = "0", name = "page") int page,
    @RequestParam(name = "size", defaultValue = "20") int size,
    @RequestParam(name = "sort", defaultValue = "id,asc") String sort
) {
    // Prevent too large page sizes
    if (size > 100) {
        size = 100;  // Maximum allowed
    }
    if (size < 1) {
        size = 1;    // Minimum allowed
    }
    
    // Continue with normal processing...
}
```

### 4. Multiple Sort Fields

```java
// URL: /api/v1/students?sort=name,asc&sort=createdAt,desc
@RequestParam(name = "sort", defaultValue = "id,asc") String[] sortParams

// Processing multiple sorts
List<Sort.Order> orders = new ArrayList<>();
for (String sortParam : sortParams) {
    String[] parts = sortParam.split(",");
    String field = parts[0];
    Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1]) ? 
        Sort.Direction.DESC : Sort.Direction.ASC;
    orders.add(new Sort.Order(direction, field));
}

Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
```

### 5. Error Handling

```java
@GetMapping("/search")
public ResponseEntity<Page<StudentDTO>> searchStudents(
    @RequestParam(defaultValue = "0", name = "page") int page,
    @RequestParam(name = "size", defaultValue = "20") int size,
    @RequestParam(name = "sort", defaultValue = "id,asc") String sort
) {
    try {
        // Validate parameters
        if (page < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page number cannot be negative");
        }
        if (size <= 0 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page size must be between 1 and 100");
        }
        
        // Process sorting
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        
        // Validate sort field (whitelist approach)
        List<String> allowedSortFields = Arrays.asList("id", "name", "createdAt", "phone");
        if (!allowedSortFields.contains(sortField)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Invalid sort field. Allowed fields: " + allowedSortFields);
        }
        
        // Continue with normal processing...
        
    } catch (ResponseStatusException e) {
        throw e;  // Re-throw validation errors
    } catch (Exception e) {
        log.error("Unexpected error in student search", e);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Search failed");
    }
}
```

---

## Troubleshooting Common Issues

### 1. "No property found for type" Error
```
Caused by: org.springframework.data.mapping.PropertyReferenceException: 
No property createdAt found for type Student
```

**Solution**: Verify the property name matches your entity exactly:
```java
// Entity field
@Column(name = "created_at")
private OffsetDateTime createdAt;  // Use this name in sort parameter

// Correct sort parameter
sort=createdAt,asc  // Not created_at
```

### 2. Page Numbers Starting from 1 Instead of 0
Spring Data uses 0-based pagination by default.
```java
// If you want 1-based pagination for users, convert in controller:
int zeroBasedPage = Math.max(0, page - 1);  // Convert 1-based to 0-based
Pageable pageable = PageRequest.of(zeroBasedPage, size, sort);
```

### 3. Losing Pagination Metadata in Response
```java
// Wrong - loses pagination metadata
List<StudentDTO> students = studentService.getStudents(pageable).getContent();
return ResponseEntity.ok(students);

// Correct - keeps pagination metadata
Page<StudentDTO> studentsPage = studentService.getStudents(pageable);
return ResponseEntity.ok(studentsPage);
```

### 4. Sort Parameter Not Working
Check that:
1. Property name matches entity field exactly
2. Property is accessible (not private without getter)
3. Property exists in the entity class

### 5. Performance Issues with Large Datasets
```java
// For very large datasets, consider:
1. Database indexing on sort fields
2. Limiting maximum page size
3. Using cursor-based pagination for better performance
4. Adding database query optimization
```

### 6. Null Pointer Exception in Repository
```java
// Ensure proper null handling in queries
@Query("SELECT s FROM Student s WHERE " +
       "(:name IS NULL OR LOWER(s.name) LIKE LOWER(:name))")  // ← Proper null handling
```

---

## Complete Working Example

Here's a complete example showing all layers working together:

### Entity
```java
@Entity
@Table(name = "student")
public class Student extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String name;
    private String phone;
    
    // Getters and setters...
}
```

### Controller
```java
@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    
    @GetMapping
    public ResponseEntity<Page<StudentDTO>> getStudents(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id,asc") String sort,
        @RequestParam(required = false) String name
    ) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1]) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));
        
        Page<StudentDTO> students = studentService.getStudents(name, pageable);
        return ResponseEntity.ok(students);
    }
}
```

### Service
```java
@Service
@Transactional
public class StudentService {
    
    @Transactional(readOnly = true)
    public Page<StudentDTO> getStudents(String name, Pageable pageable) {
        Page<Student> studentsPage = studentRepository.findStudents(name, pageable);
        return studentsPage.map(studentMapper::toDto);
    }
}
```

### Repository
```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    
    @Query("SELECT s FROM Student s WHERE s.endAt IS NULL AND " +
           "(:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Student> findStudents(@Param("name") String name, Pageable pageable);
}
```

### Usage
```bash
# Get first page (20 students)
GET /api/v1/students

# Get second page with 10 students per page
GET /api/v1/students?page=1&size=10

# Search and sort by name descending
GET /api/v1/students?name=john&sort=name,desc

# Complex example
GET /api/v1/students?name=smith&page=2&size=15&sort=createdAt,desc
```

This guide covers everything you need to implement robust pagination in your Spring Boot application following the patterns already established in your codebase!