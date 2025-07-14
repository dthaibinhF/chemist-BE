# APIs in Chemist-BE Project

This document provides an overview of the APIs in the Chemist-BE project.

## API Structure

The APIs in the Chemist-BE project follow a RESTful design pattern. All API endpoints are prefixed with `/api/v1/` followed by the resource name.

## Common Patterns

Most controllers follow these common patterns:

1. **GET /api/v1/{resource}** - Get all resources
2. **GET /api/v1/{resource}/{id}** - Get a specific resource by ID
3. **POST /api/v1/{resource}** - Create a new resource
4. **PUT /api/v1/{resource}/{id}** - Update an existing resource
5. **DELETE /api/v1/{resource}/{id}** - Delete a resource (usually soft delete)

## API Endpoints

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

### Schedule API

**Base Path**: `/api/v1/schedule`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all schedules with optional filtering by group, start date, and end date |
| GET | `/{id}` | Get a schedule by ID |
| POST | `/` | Create a new schedule |
| PUT | `/{id}` | Update a schedule |
| DELETE | `/{id}` | Delete a schedule |
| POST | `/weekly` | Generate weekly schedules for a group based on its group schedule templates |

#### Query Parameters for GET /api/v1/schedule
- `groupId` (optional): Filter schedules by group ID
- `startDate` (optional): Filter schedules by start date
- `endDate` (optional): Filter schedules by end date
- Pagination parameters (e.g., `page`, `size`, `sort`)

#### Query Parameters for POST /api/v1/schedule/weekly
- `groupId`: The ID of the group for which to generate schedules
- `startDate`: The start date for the schedule generation period
- `endDate`: The end date for the schedule generation period

### Other APIs

The project includes many other APIs for different resources. Here's a list of the identified controllers:

1. **AcademicYearController**: Manages academic years
2. **AccountController**: Manages user accounts
3. **AttendanceController**: Manages student attendance
4. **AuthController**: Handles authentication
5. **DemoController**: Demo/test endpoints
6. **ExamController**: Manages exams
7. **FeeController**: Manages fees
8. **GradeController**: Manages grades
9. **GroupController**: Manages student groups
10. **GroupScheduleController**: Manages group schedules
11. **PaymentDetailController**: Manages payment details
12. **RoleController**: Manages user roles
13. **RoomController**: Manages rooms
14. **ScheduleController**: Manages schedules (see Schedule API section for details)
15. **SchoolClassController**: Manages school classes
16. **SchoolController**: Manages schools
17. **ScoreController**: Manages student scores
18. **StudentController**: Manages students
19. **TeacherController**: Manages teachers

Details for each API will be added as they are explored.

## Authentication

The project includes an AuthController, suggesting that authentication is required for API access. More details will be added as the authentication mechanism is explored.

## Notes

This document will be updated as more information about the APIs is gathered.
