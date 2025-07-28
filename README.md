# Chemist-BE

A comprehensive Spring Boot REST API application for managing educational data including students, teachers, schedules, grades, and academic operations.

## 🚀 Features

- **Student Management**: Complete CRUD operations with advanced search capabilities
- **Teacher Management**: Teacher profiles with search functionality
- **Schedule Management**: Weekly schedule generation and management
- **Group Sessions**: Group-based learning session management
- **Attendance Tracking**: Bulk attendance operations and real-time tracking
- **Grade Management**: Academic performance tracking and evaluation
- **Statistics Dashboard**: Real-time analytics and reporting
- **JWT Authentication**: Secure role-based access control (ADMIN, TEACHER, MANAGER)
- **Soft Delete**: Data preservation with audit trail support

## 🏗️ Architecture

Built with a clean 6-layer architecture pattern:

1. **Entity Layer** - JPA entities with soft delete support
2. **DTO Layer** - Data transfer objects with JSON snake_case mapping
3. **Mapper Layer** - MapStruct automatic mapping
4. **Repository Layer** - JPA repositories with custom queries
5. **Service Layer** - Business logic with transaction management
6. **Controller Layer** - REST endpoints with `/api/v1/` prefix

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.4.7
- **Java Version**: Java 21
- **Database**: PostgreSQL with Flyway migrations
- **Security**: Spring Security with JWT authentication
- **Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Caching**: Caffeine Cache for performance optimization
- **Testing**: Spring Boot Test with H2 in-memory database
- **Additional Technologies**:
  - Blaze Persistence for advanced queries
  - MapStruct for DTO mapping
  - Lombok for code generation
  - HikariCP for connection pooling

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+ 
- Git

## ⚙️ Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd chemist-BE
```

### 2. Database Setup
Create a PostgreSQL database named `chemist`:
```sql
CREATE DATABASE chemist;
```

### 3. Environment Configuration
Set the following environment variables or use default values:
```bash
export DB_USERNAME=postgres
export DB_PASSWORD=root
```

Default database connection:
- **Host**: localhost:5432
- **Database**: chemist
- **Username**: postgres
- **Password**: root

### 4. Build and Run
```bash
# Compile and generate MapStruct mappers
./mvnw clean compile

# Run tests
./mvnw test

# Start the application
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`

## 📚 API Documentation

### Swagger UI
Visit `http://localhost:8080/swagger-ui.html` for interactive API documentation.

### Key Endpoints

#### Authentication
- `POST /api/v1/auth/login` - User authentication
- `POST /api/v1/auth/refresh` - Token refresh

#### Student Management
- `GET /api/v1/students` - List all students
- `GET /api/v1/students/search` - Advanced student search
- `POST /api/v1/students` - Create new student
- `PUT /api/v1/students/{id}` - Update student
- `DELETE /api/v1/students/{id}` - Soft delete student

#### Teacher Management
- `GET /api/v1/teachers` - List all teachers
- `GET /api/v1/teachers/search` - Search teachers by name, phone, email
- `POST /api/v1/teachers` - Create new teacher

#### Schedule Management
- `GET /api/v1/schedules` - List schedules
- `POST /api/v1/schedules/generate-weekly` - Generate weekly schedules
- `GET /api/v1/schedules/search` - Search schedules with filters

#### Attendance Management
- `POST /api/v1/attendance/bulk` - Bulk create attendance records
- `PUT /api/v1/attendance/bulk` - Bulk update attendance records

#### Statistics
- `GET /api/v1/statistics/dashboard` - Dashboard statistics

## 🔐 Security

### JWT Authentication
- **Access Token**: 1-hour expiration
- **Refresh Token**: 7-day expiration
- **Authorization Header**: `Bearer <token>`

### Role-Based Access Control
- **ADMIN**: Full system access
- **TEACHER**: Teaching-related operations
- **MANAGER**: Management operations

### CORS Configuration
Configured for frontend development:
- localhost:3000 (React)
- localhost:3005 (Next.js)
- localhost:5173 (Vite)
- Netlify deployment domains

## 🚀 Development

### Maven Commands
```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Run specific test
./mvnw test -Dtest=ClassName#methodName

# Build JAR
./mvnw clean package

# Start application
./mvnw spring-boot:run
```

### Development Profiles
- `dev` - Development profile (default)
- `test` - Testing profile with H2 database

### Database Migrations
Flyway migrations are located in `src/main/resources/db/migration/`

## 🎯 Key Features

### Performance Optimization
- **Caffeine Cache**: 30-minute TTL for static data
- **Connection Pooling**: HikariCP configuration
- **Query Optimization**: Blaze Persistence for complex queries

### Data Management
- **Soft Delete**: All entities use `endAt` field for data preservation
- **Audit Trail**: Automatic timestamps with `BaseEntity`
- **Timezone Support**: Asia/Ho_Chi_Minh timezone handling

### API Design
- **RESTful**: Consistent REST API patterns
- **Pagination**: Built-in pagination support
- **Search & Filter**: Advanced query capabilities
- **Bulk Operations**: Efficient bulk data operations

## 🧪 Testing

The project includes comprehensive integration tests:

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

Testing approach:
- **Integration Tests**: Endpoint testing with MockMvc
- **Database**: H2 in-memory database for tests
- **Security**: Mock authentication for protected endpoints

## 📊 Monitoring

### Health Checks
- Actuator endpoints available at `/actuator/health`
- Database connection monitoring
- Application metrics

### Logging
- Structured logging with proper levels
- Request/response logging for debugging
- Error tracking and monitoring

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow existing code conventions
- Use proper annotation processor setup for Lombok + MapStruct
- Implement soft delete for all entities
- Follow the 6-layer architecture pattern

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions, please open an issue in the repository.

---

**Made with ❤️ using Spring Boot and Java 21**