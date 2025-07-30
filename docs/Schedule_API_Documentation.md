# Schedule API Documentation

## Overview
The Schedule API provides endpoints for managing class schedules in the Chemist-BE system. It supports full CRUD operations, advanced search with filtering, and automated weekly schedule generation from templates.

**Base URL**: `/api/v1/schedule`  
**Timezone**: Asia/Ho_Chi_Minh (UTC+7)  
**Soft Delete**: All operations respect soft delete patterns

---

## Data Models

### ScheduleDTO Structure
```json
{
  "id": 1,
  "group_id": 1,
  "group_name": "Math Grade 10A",
  "start_time": "2025-07-25T08:00:00+07:00",
  "end_time": "2025-07-25T10:00:00+07:00",
  "delivery_mode": "ONLINE",
  "meeting_link": "https://zoom.us/j/123456789",
  "teacher": {
    "id": 1,
    "account": { /* AccountDTO */ },
    "teacher_details": [ /* TeacherDetailDTO[] */ ],
    "schedules": [ /* ScheduleDTO[] */ ]
  },
  "room": {
    "id": 1,
    "name": "Room A101",
    "location": "Building A, Floor 1",
    "capacity": 30
  },
  "attendances": [
    {
      "id": 1,
      "schedule_id": 1,
      "student_id": 1,
      "student_name": "John Doe",
      "status": "PRESENT",
      "description": null
    }
  ],
  "created_at": "2025-07-25T12:00:00+07:00",
  "updated_at": "2025-07-25T12:00:00+07:00"
}
```

### Field Definitions
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `id` | Integer | No (auto-generated) | Schedule unique identifier |
| `group_id` | Integer | **Yes** | Associated group ID |
| `group_name` | String | No (auto-populated) | Group name for display |
| `start_time` | OffsetDateTime | **Yes** | Schedule start time with timezone |
| `end_time` | OffsetDateTime | **Yes** | Schedule end time with timezone |
| `delivery_mode` | String | **Yes** | Delivery mode: "ONLINE", "OFFLINE", "HYBRID" |
| `meeting_link` | String | Conditional | Required when delivery_mode is "ONLINE" |
| `teacher` | TeacherDTO | No | Assigned teacher (optional) |
| `room` | RoomDTO | **Yes** | Assigned room |
| `attendances` | AttendanceDTO[] | No | Associated attendance records |

---

## API Endpoints

### 1. Get All Schedules
**GET** `/api/v1/schedule`

Returns all active schedules without pagination.

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "group_id": 1,
    "group_name": "Math Grade 10A",
    "start_time": "2025-07-25T08:00:00+07:00",
    "end_time": "2025-07-25T10:00:00+07:00",
    "delivery_mode": "OFFLINE",
    "meeting_link": null,
    "teacher": { /* TeacherDTO */ },
    "room": { /* RoomDTO */ },
    "attendances": []
  }
]
```

### 2. Search Schedules (Paginated)
**GET** `/api/v1/schedule/search`

Search schedules with filtering and pagination support.

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `page` | Integer | No | Page number (0-based, default: 0) |
| `size` | Integer | No | Page size (default: 20) |
| `sort` | String | No | Sort field and direction (e.g., "startTime,desc") |
| `groupId` | Integer | No | Filter by group ID |
| `startDate` | LocalDate | No | Filter schedules starting from this date |
| `endDate` | LocalDate | No | Filter schedules ending before this date |

**Date Format**: `YYYY-MM-DD` (e.g., "2025-07-25")

**Example Requests**:
```bash
# Basic pagination
GET /api/v1/schedule/search?page=0&size=10

# Filter by group
GET /api/v1/schedule/search?groupId=1&page=0&size=5

# Date range filter
GET /api/v1/schedule/search?startDate=2025-07-25&endDate=2025-07-31

# Complex query with sorting
GET /api/v1/schedule/search?groupId=1&startDate=2025-07-25&sort=startTime,desc&size=10
```

**Response**: `200 OK`
```json
[
  {
    "id": 1,
    "group_id": 1,
    "start_time": "2025-07-25T08:00:00+07:00",
    "end_time": "2025-07-25T10:00:00+07:00",
    "delivery_mode": "ONLINE",
    "meeting_link": "https://zoom.us/j/123456789"
  }
]
```

**Note**: Returns only content array, not full pagination metadata.

### 3. Get Schedule by ID
**GET** `/api/v1/schedule/{id}`

Retrieve a specific schedule by its ID.

**Path Parameters**:
- `id` (Integer): Schedule ID

**Response**: `200 OK`
```json
{
  "id": 1,
  "group_id": 1,
  "group_name": "Math Grade 10A",
  "start_time": "2025-07-25T08:00:00+07:00",
  "end_time": "2025-07-25T10:00:00+07:00",
  "delivery_mode": "OFFLINE",
  "meeting_link": null,
  "teacher": { /* TeacherDTO */ },
  "room": { /* RoomDTO */ },
  "attendances": []
}
```

### 4. Create Schedule
**POST** `/api/v1/schedule`

Create a new schedule with validation and conflict checking.

**Request Body**:
```json
{
  "group_id": 1,
  "start_time": "2025-07-25T08:00:00+07:00",
  "end_time": "2025-07-25T10:00:00+07:00",
  "delivery_mode": "ONLINE",
  "meeting_link": "https://zoom.us/j/123456789",
  "teacher": {
    "id": 1
  },
  "room": {
    "id": 1
  }
}
```

**Validation Rules**:
- `start_time` must be before `end_time`
- `delivery_mode` must be one of: "ONLINE", "OFFLINE", "HYBRID"
- `meeting_link` is required when `delivery_mode` is "ONLINE"
- `group_id` and `room` are required
- Room and teacher availability will be checked for conflicts

**Response**: `200 OK`
```json
{
  "id": 123,
  "group_id": 1,
  "start_time": "2025-07-25T08:00:00+07:00",
  "end_time": "2025-07-25T10:00:00+07:00",
  "delivery_mode": "ONLINE",
  "meeting_link": "https://zoom.us/j/123456789",
  "created_at": "2025-07-25T12:00:00+07:00",
  "updated_at": "2025-07-25T12:00:00+07:00"
}
```

### 5. Update Schedule
**PUT** `/api/v1/schedule/{id}`

Update an existing schedule with the same validation as creation.

**Path Parameters**:
- `id` (Integer): Schedule ID to update

**Request Body**: Same as Create Schedule

**Response**: `200 OK` (Updated ScheduleDTO)

### 6. Delete Schedule
**DELETE** `/api/v1/schedule/{id}`

Soft delete a schedule (sets `end_at` timestamp).

**Path Parameters**:
- `id` (Integer): Schedule ID to delete

**Response**: `204 No Content`

### 7. Generate Weekly Schedule
**POST** `/api/v1/schedule/weekly`

Generate weekly schedules for a group based on GroupSchedule templates.

**Query Parameters**:
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `groupId` | Integer | **Yes** | Group ID to generate schedules for |
| `startDate` | LocalDate | **Yes** | Start date for generation |
| `endDate` | LocalDate | **Yes** | End date for generation |

**Example Request**:
```bash
POST /api/v1/schedule/weekly?groupId=1&startDate=2025-07-25&endDate=2025-08-01
```

**Algorithm**:
1. Validates parameters and group existence
2. Checks for existing schedules in date range (prevents duplicates)
3. Retrieves GroupSchedule templates for the group
4. For each template, finds matching days in the date range
5. Creates Schedule instances with proper timezone conversion
6. Validates each schedule for room/teacher conflicts
7. Saves all valid schedules in a single transaction

**Response**: `200 OK`
```json
[
  {
    "id": 124,
    "group_id": 1,
    "start_time": "2025-07-25T08:00:00+07:00",
    "end_time": "2025-07-25T10:00:00+07:00",
    "delivery_mode": "OFFLINE",
    "room": { /* RoomDTO from template */ }
  },
  {
    "id": 125,
    "group_id": 1,
    "start_time": "2025-07-27T08:00:00+07:00",
    "end_time": "2025-07-27T10:00:00+07:00",
    "delivery_mode": "OFFLINE",
    "room": { /* RoomDTO from template */ }
  }
]
```

**Notes**:
- Returns empty array if group has no templates
- Skips schedules that would create conflicts
- Generated schedules have default `delivery_mode: "OFFLINE"`
- Teachers are not assigned (set to null)

---

## Error Responses

### Error Response Format
```json
{
  "timestamp": "2025-07-25T12:00:00+07:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Start time must be before end time",
  "path": "/api/v1/schedule"
}
```

### HTTP Status Codes

#### 400 Bad Request
**Triggers**:
- Invalid date ranges (start after end)
- Invalid delivery mode
- Missing required fields
- Missing meeting link for online mode
- Invalid time order (start_time after end_time)
- Past dates for weekly generation

**Example Messages**:
- `"Start date must be before end date"`
- `"Start time must be before end time"`
- `"Invalid delivery mode"`
- `"Meeting link required for online delivery"`
- `"Group ID is required"`
- `"Room is required"`
- `"Start date cannot be in the past"`

#### 404 Not Found
**Triggers**:
- Schedule ID doesn't exist
- Referenced group doesn't exist
- Referenced room doesn't exist
- Referenced teacher doesn't exist

**Example Messages**:
- `"Schedule not found: 123"`
- `"Group not found: 456"`
- `"Room not found: 789"`
- `"Teacher not found: 101"`

#### 409 Conflict
**Triggers**:
- Room already booked for the time slot
- Teacher already assigned during the time slot
- Group already has schedules in the date range (weekly generation)

**Example Messages**:
- `"Room is already booked for the specified time"`
- `"Teacher is already assigned for the specified time"`
- `"Group 1 already has 5 schedules between 2025-07-25 and 2025-07-31. Remove existing schedules first."`

#### 500 Internal Server Error
**Triggers**:
- Database connection issues
- Unexpected system errors
- Transaction failures

**Example Messages**:
- `"Failed to create schedule"`
- `"Failed to update schedule"`
- `"Failed to generate weekly schedule"`

---

## Business Rules

### Validation Rules
1. **Time Validation**: `start_time` must be before `end_time`
2. **Delivery Mode**: Must be "ONLINE", "OFFLINE", or "HYBRID"
3. **Meeting Link**: Required when delivery_mode is "ONLINE"
4. **Required Fields**: group_id, start_time, end_time, delivery_mode, room
5. **Entity Existence**: All referenced entities (group, room, teacher) must exist and be active

### Conflict Detection
1. **Room Conflicts**: No double-booking of rooms for overlapping times
2. **Teacher Conflicts**: No double-assignment of teachers for overlapping times
3. **Time Overlap Logic**: Detects any intersection between time ranges
4. **Update Exclusion**: Current schedule excluded from conflict check during updates

### Weekly Generation Rules
1. **Template Dependency**: Group must have GroupSchedule templates configured
2. **No Duplicates**: Existing schedules in date range prevent generation
3. **Date Matching**: Only generates schedules for days that match template day-of-week
4. **Conflict Skipping**: Invalid schedules (conflicts) are skipped, not failed
5. **Default Values**: Generated schedules use "OFFLINE" mode with no teacher assigned

### Soft Delete Behavior
1. **Active Only**: All queries filter out soft-deleted records
2. **Cascade**: Related entities (group, room, teacher) must also be active
3. **Deletion**: Sets `end_at` timestamp instead of physical deletion
4. **Recovery**: Soft-deleted schedules can be recovered by clearing `end_at`

---

## Database Schema

### Schedule Table
```sql
Table: schedule
- id (INTEGER, PRIMARY KEY, AUTO_INCREMENT)
- group_id (INTEGER, NOT NULL, FK -> group.id)
- start_time (TIMESTAMP WITH TIME ZONE, NOT NULL)
- end_time (TIMESTAMP WITH TIME ZONE, NOT NULL)
- delivery_mode (VARCHAR(20), NOT NULL)
- meeting_link (TEXT, NULLABLE)
- teacher_id (INTEGER, NULLABLE, FK -> teacher.id)
- room_id (INTEGER, NULLABLE, FK -> room.id)
- created_at (TIMESTAMP WITH TIME ZONE)
- updated_at (TIMESTAMP WITH TIME ZONE)
- end_at (TIMESTAMP WITH TIME ZONE) -- Soft delete
```

### Relationships
- **Group**: Many-to-One (required)
- **Teacher**: Many-to-One (optional)
- **Room**: Many-to-One (optional in entity, required by business logic)
- **Attendance**: One-to-Many (schedules can have multiple attendance records)

### Indexes
- Primary key on `id`
- Foreign key indexes on `group_id`, `teacher_id`, `room_id`
- Consider adding composite index on `(start_time, end_time, room_id)` for conflict queries

---

## Performance Considerations

### Query Optimization
1. **Soft Delete Filtering**: All queries include `(end_at IS NULL OR end_at > current_timestamp)`
2. **Native SQL**: Search endpoint uses native SQL for better performance
3. **Lazy Loading**: Related entities use `FetchType.LAZY`
4. **Batch Operations**: Weekly generation uses `saveAll()` for efficiency

### Caching Strategy
- Consider caching frequently accessed room and teacher data
- GroupSchedule templates could be cached as they change infrequently
- Schedule data typically not cached due to frequent updates

### Pagination
- Default page size: 20 records
- Search endpoint supports custom page sizes
- Uses Spring Data pagination with proper count queries

---

## Usage Examples

### Frontend Integration

#### Create a New Online Schedule
```javascript
const createSchedule = async (scheduleData) => {
  const response = await fetch('/api/v1/schedule', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      group_id: 1,
      start_time: '2025-07-25T08:00:00+07:00',
      end_time: '2025-07-25T10:00:00+07:00',
      delivery_mode: 'ONLINE',
      meeting_link: 'https://zoom.us/j/123456789',
      room: { id: 1 },
      teacher: { id: 1 }
    })
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }
  
  return response.json();
};
```

#### Search with Filters
```javascript
const searchSchedules = async (filters) => {
  const params = new URLSearchParams();
  
  if (filters.groupId) params.append('groupId', filters.groupId);
  if (filters.startDate) params.append('startDate', filters.startDate);
  if (filters.endDate) params.append('endDate', filters.endDate);
  if (filters.page) params.append('page', filters.page);
  if (filters.size) params.append('size', filters.size);
  
  const response = await fetch(`/api/v1/schedule/search?${params}`);
  return response.json();
};
```

#### Generate Weekly Schedules
```javascript
const generateWeeklySchedule = async (groupId, startDate, endDate) => {
  const params = new URLSearchParams({
    groupId: groupId,
    startDate: startDate,
    endDate: endDate
  });
  
  const response = await fetch(`/api/v1/schedule/weekly?${params}`, {
    method: 'POST'
  });
  
  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message);
  }
  
  return response.json();
};
```

---

## Testing Guidelines

### Unit Testing
- Test all validation rules in `ScheduleService`
- Mock repository calls for isolation
- Test error handling for all edge cases
- Verify timezone conversion logic

### Integration Testing
- Test complete CRUD operations through REST endpoints
- Verify conflict detection with real database
- Test pagination and filtering functionality
- Test weekly generation with various scenarios

### Test Data Setup
```sql
-- Insert test group
INSERT INTO "group" (id, name, created_at) VALUES (1, 'Test Group', NOW());

-- Insert test room
INSERT INTO room (id, name, location, capacity, created_at) VALUES (1, 'Room A101', 'Building A', 30, NOW());

-- Insert test teacher
INSERT INTO teacher (id, created_at) VALUES (1, NOW());

-- Insert GroupSchedule template
INSERT INTO group_schedule (group_id, day_of_week, start_time, end_time, room_id, created_at) 
VALUES (1, 'MONDAY', '08:00:00', '10:00:00', 1, NOW());
```

---

## Migration and Deployment

### Database Migration
Ensure proper database schema with all foreign key constraints and indexes before deployment.

### Environment Configuration
- Set correct timezone in `application.properties`: `spring.jackson.time-zone=Asia/Ho_Chi_Minh`
- Configure database connection settings
- Set up proper logging levels for debugging

### Monitoring
- Monitor API response times, especially for search and weekly generation
- Track error rates for validation and conflict scenarios
- Monitor database query performance for complex searches

---

This documentation provides a complete reference for the Schedule API, including all endpoints, data models, validation rules, error handling, and usage examples for frontend integration.