# API Documentation for Chemist-BE Project

This document provides detailed documentation for the RESTful APIs in the Chemist-BE project.

## 1. List of RESTful API Endpoints

All API endpoints are prefixed with `/api/v1/` followed by the resource name. The base URL for all API endpoints is `http://localhost:8080`.

### Student API

**Base Path**: `/api/v1/student`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all students |
| GET | `/{id}` | Get a student by ID |
| POST | `/` | Create a new student |
| POST | `/multiple` | Create multiple students |
| PUT | `/{id}` | Update a student |
| DELETE | `/{id}` | Delete a student |
| GET | `/by-group/{groupId}` | Get students by group ID |
| GET | `/{studentId}/detail-history` | Get history of student details |

### Teacher API

**Base Path**: `/api/v1/teacher`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all teachers |
| GET | `/{id}` | Get a teacher by ID |
| POST | `/` | Create a new teacher |
| PUT | `/{id}` | Update a teacher |
| DELETE | `/{id}` | Delete a teacher |

### Academic Year API

**Base Path**: `/api/v1/academic-year`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all academic years |
| GET | `/{id}` | Get an academic year by ID |
| POST | `/` | Create a new academic year |
| PUT | `/{id}` | Update an academic year |
| DELETE | `/{id}` | Delete an academic year |

### Attendance API

**Base Path**: `/api/v1/attendance`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all attendances |
| GET | `/{id}` | Get an attendance by ID |
| POST | `/` | Create a new attendance |
| PUT | `/{id}` | Update an attendance |
| DELETE | `/{id}` | Delete an attendance |
| GET | `/search` | Search attendance by group and schedule |

### Exam API

**Base Path**: `/api/v1/exam`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all exams |
| GET | `/{id}` | Get an exam by ID |
| POST | `/` | Create a new exam |
| PUT | `/{id}` | Update an exam |
| DELETE | `/{id}` | Delete an exam |

### Fee API

**Base Path**: `/api/v1/fee`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all fees |
| GET | `/{id}` | Get a fee by ID |
| POST | `/` | Create a new fee |
| PUT | `/{id}` | Update a fee |
| DELETE | `/{id}` | Delete a fee |

### Grade API

**Base Path**: `/api/v1/grade`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all grades |
| GET | `/{id}` | Get a grade by ID |
| POST | `/` | Create a new grade |
| PUT | `/{id}` | Update a grade |
| DELETE | `/{id}` | Delete a grade |

### Group API

**Base Path**: `/api/v1/group`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all groups |
| GET | `/{id}` | Get a group by ID |
| POST | `/` | Create a new group |
| PUT | `/{id}` | Update a group |
| DELETE | `/{id}` | Delete a group |
| GET | `/detail` | Get all groups with detail |
| GET | `/academic-year/{academicYearId}` | Get groups by academic year ID |
| GET | `/grade/{gradeId}` | Get groups by grade ID |

### Group Schedule API

**Base Path**: `/api/v1/group-schedule`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all group schedules |
| GET | `/{id}` | Get a group schedule by ID |
| POST | `/` | Create a new group schedule |
| PUT | `/{id}` | Update a group schedule |
| DELETE | `/{id}` | Delete a group schedule |
| GET | `/group/{groupId}` | Get group schedules by group ID |

### Schedule API

**Base Path**: `/api/v1/schedule`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all schedules |
| GET | `/search` | Get schedules with pagination and filtering |
| GET | `/{id}` | Get a schedule by ID |
| POST | `/` | Create a new schedule |
| PUT | `/{id}` | Update a schedule |
| DELETE | `/{id}` | Delete a schedule |
| POST | `/weekly` | Generate weekly schedules |

### Score API

**Base Path**: `/api/v1/score`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all scores |
| GET | `/{id}` | Get a score by ID |
| POST | `/` | Create a new score |
| PUT | `/{id}` | Update a score |
| DELETE | `/{id}` | Delete a score |
| GET | `/exam/{examId}` | Get scores by exam ID |
| GET | `/student/{studentId}` | Get scores by student ID |

### Authentication API

**Base Path**: `/api/v1/auth`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/register` | Register a new user |
| POST | `/login` | Authenticate a user |
| GET | `/me` | Get the current user's account information |
| POST | `/refresh-token` | Refresh the authentication token |

## 2. Parameters for Each Endpoint

### Student API

#### GET /api/v1/student
- No parameters required

#### GET /api/v1/student/{id}
- **Path Parameters**:
  - `id` (Integer, required): The ID of the student to retrieve

#### POST /api/v1/student
- **Request Body** (required):
  - `first_name` (String, required): The student's first name
  - `last_name` (String, required): The student's last name
  - `date_of_birth` (String, required): The student's date of birth in ISO format (YYYY-MM-DD)
  - `email` (String, optional): The student's email address
  - `phone` (String, optional): The student's phone number
  - `address` (String, optional): The student's address
  - `group_id` (Integer, optional): The ID of the group the student belongs to

#### POST /api/v1/student/multiple
- **Request Body** (required): An array of student objects, each with the same fields as in POST /api/v1/student

#### PUT /api/v1/student/{id}
- **Path Parameters**:
  - `id` (Integer, required): The ID of the student to update
- **Request Body** (required): Same fields as in POST /api/v1/student

#### DELETE /api/v1/student/{id}
- **Path Parameters**:
  - `id` (Integer, required): The ID of the student to delete

#### GET /api/v1/student/by-group/{groupId}
- **Path Parameters**:
  - `groupId` (Integer, required): The ID of the group to get students for

#### GET /api/v1/student/{studentId}/detail-history
- **Path Parameters**:
  - `studentId` (Integer, required): The ID of the student to get detail history for

### Schedule API

#### GET /api/v1/schedule/search
- **Query Parameters**:
  - `groupId` (Integer, optional): Filter schedules by group ID
  - `startDate` (OffsetDateTime, optional): Filter schedules by start date
  - `endDate` (OffsetDateTime, optional): Filter schedules by end date
  - `page` (Integer, optional): Page number for pagination (default: 0)
  - `size` (Integer, optional): Number of items per page (default: 20)
  - `sort` (String, optional): Sort field and direction (e.g., "startTime,asc")

#### POST /api/v1/schedule/weekly
- **Query Parameters**:
  - `groupId` (Integer, required): The ID of the group for which to generate schedules
  - `startDate` (OffsetDateTime, required): The start date for the schedule generation period
  - `endDate` (OffsetDateTime, required): The end date for the schedule generation period

### Attendance API

#### GET /api/v1/attendance/search
- **Query Parameters**:
  - `groupId` (Integer, optional): Filter attendances by group ID
  - `scheduleId` (Integer, optional): Filter attendances by schedule ID

## 3. Example Requests and Responses

### Creating a Student

**Request**:
```http
POST http://localhost:8080/api/v1/student
Content-Type: application/json

{
  "first_name": "John",
  "last_name": "Doe",
  "date_of_birth": "2005-05-15",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "address": "123 Main St",
  "group_id": 1
}
```

**Response**:
```json
{
  "id": 1,
  "first_name": "John",
  "last_name": "Doe",
  "date_of_birth": "2005-05-15",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "address": "123 Main St",
  "group_id": 1,
  "created_at": "2023-06-15T10:30:00Z",
  "updated_at": "2023-06-15T10:30:00Z"
}
```

### Getting a Student

**Request**:
```http
GET http://localhost:8080/api/v1/student/1
```

**Response**:
```json
{
  "id": 1,
  "first_name": "John",
  "last_name": "Doe",
  "date_of_birth": "2005-05-15",
  "email": "john.doe@example.com",
  "phone": "1234567890",
  "address": "123 Main St",
  "group_id": 1,
  "created_at": "2023-06-15T10:30:00Z",
  "updated_at": "2023-06-15T10:30:00Z"
}
```

### Authenticating a User

**Request**:
```http
POST http://localhost:8080/api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

**Response**:
```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_at": "2023-06-15T11:30:00Z"
}
```

### Searching Schedules

**Request**:
```http
GET http://localhost:8080/api/v1/schedule/search?groupId=1&startDate=2023-06-01T00:00:00Z&endDate=2023-06-30T23:59:59Z&page=0&size=10&sort=startTime,asc
```

**Response**:
```json
[
  {
    "id": 1,
    "group_id": 1,
    "teacher_id": 2,
    "room_id": 3,
    "subject": "Mathematics",
    "start_time": "2023-06-15T09:00:00Z",
    "end_time": "2023-06-15T10:30:00Z",
    "created_at": "2023-06-01T10:30:00Z",
    "updated_at": "2023-06-01T10:30:00Z"
  },
  {
    "id": 2,
    "group_id": 1,
    "teacher_id": 3,
    "room_id": 4,
    "subject": "Physics",
    "start_time": "2023-06-15T11:00:00Z",
    "end_time": "2023-06-15T12:30:00Z",
    "created_at": "2023-06-01T10:35:00Z",
    "updated_at": "2023-06-01T10:35:00Z"
  }
]
```

## 4. Naming Rules, Filtering, and Pagination

### Naming Rules

- All API endpoints follow the RESTful convention
- Base path for all endpoints is `/api/v1/`
- Resource names are in kebab-case for multi-word resources (e.g., `academic-year`, `group-schedule`)
- Query parameters are in camelCase (e.g., `groupId`, `startDate`)
- JSON property names in responses are in snake_case (e.g., `first_name`, `date_of_birth`)

### Filtering

Many endpoints support filtering through query parameters:

- **Student API**: Filter by group ID using the `/by-group/{groupId}` endpoint
- **Schedule API**: Filter by group ID, start date, and end date using query parameters
- **Attendance API**: Filter by group ID and schedule ID using query parameters
- **Group API**: Filter by academic year ID or grade ID using dedicated endpoints
- **Score API**: Filter by exam ID or student ID using dedicated endpoints

### Pagination

The Schedule API supports pagination through the following query parameters:

- `page`: The page number (0-based, default: 0)
- `size`: The number of items per page (default: 20)
- `sort`: The field to sort by, followed by the direction (e.g., "startTime,asc" or "endTime,desc")

## 5. Common Errors and Error Format

### Error Format

Error responses follow a consistent format:

```json
{
  "timestamp": "2023-06-15T10:30:00Z",
  "status": 404,
  "error": "Resource not found",
  "path": "/api/v1/student/999"
}
```

The error response includes:
- `timestamp`: The time when the error occurred
- `status`: The HTTP status code
- `error`: A human-readable error message
- `path`: The request path that caused the error

### Common Error Status Codes

- **400 Bad Request**: The request was malformed or contained invalid parameters
- **401 Unauthorized**: Authentication is required but was not provided
- **403 Forbidden**: The authenticated user does not have permission to access the requested resource
- **404 Not Found**: The requested resource was not found
- **500 Internal Server Error**: An unexpected error occurred on the server

## 6. Limitations and Special Notes

### Authentication

- Most endpoints require authentication
- Authentication is done using JWT (JSON Web Tokens)
- Tokens are obtained by calling the `/api/v1/auth/login` endpoint
- Tokens expire after a certain period and need to be refreshed using the `/api/v1/auth/refresh-token` endpoint
- Some endpoints require specific roles (e.g., ADMIN, TEACHER, MANAGER)

### Authorization

- The Academic Year API requires the user to have one of the following roles: ADMIN, TEACHER, or MANAGER
- Other APIs may have similar role-based access restrictions

### Request Validation

- Request bodies are validated using Jakarta Validation annotations
- Invalid requests will result in a 400 Bad Request response with details about the validation errors

### Soft Delete

- Delete operations are implemented as soft deletes
- Deleted resources are marked as deleted in the database but are not physically removed
- Deleted resources are not returned by default in GET requests

### Data Relationships

- Many resources have relationships with other resources
- For example, students belong to groups, schedules are associated with groups and teachers, etc.
- When creating or updating resources, make sure to provide valid IDs for related resources

### API Versioning

- All endpoints are prefixed with `/api/v1/` to indicate the API version
- Future versions of the API may use different prefixes (e.g., `/api/v2/`)
