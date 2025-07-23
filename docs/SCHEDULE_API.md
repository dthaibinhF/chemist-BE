# Schedule API Documentation

This document outlines how to fetch schedules from the Chemist-BE API.

## Base URL
```
/api/v1/schedule
```

## Endpoints

### 1. Get All Schedules

**Endpoint:** `GET /api/v1/schedule`

Fetches all schedules without pagination.

**Response:**
```json
[
  {
    "id": 1,
    "group_id": 123,
    "start_time": "2025-07-23T09:00:00+07:00",
    "end_time": "2025-07-23T10:30:00+07:00",
    "room_id": 456,
    "teacher_id": 789,
    "created_at": "2025-07-23T08:00:00+07:00",
    "updated_at": "2025-07-23T08:00:00+07:00"
  }
]
```

### 2. Get Schedules with Filters and Pagination

**Endpoint:** `GET /api/v1/schedule/search`

Fetches schedules with optional filters and pagination.

**Query Parameters:**
- `groupId` (optional): Filter by group ID
- `startDate` (optional): Filter schedules starting from this date (format: ISO-8601)
- `endDate` (optional): Filter schedules up to this date (format: ISO-8601)
- `page` (optional): Page number (default: 0)
- `size` (optional): Items per page (default: 20)

**Example Request:**
```
GET /api/v1/schedule/search?groupId=123&startDate=2025-07-23T00:00:00Z&endDate=2025-07-24T00:00:00Z&page=0&size=10
```

**Response:**
```json
[
  {
    "id": 1,
    "group_id": 123,
    "start_time": "2025-07-23T09:00:00+07:00",
    "end_time": "2025-07-23T10:30:00+07:00",
    "room_id": 456,
    "teacher_id": 789,
    "created_at": "2025-07-23T08:00:00+07:00",
    "updated_at": "2025-07-23T08:00:00+07:00"
  }
]
```

### 3. Get Schedule by ID

**Endpoint:** `GET /api/v1/schedule/{id}`

Fetches a single schedule by its ID.

**Path Parameters:**
- `id`: Schedule ID (integer)

**Example Request:**
```
GET /api/v1/schedule/1
```

**Response:**
```json
{
  "id": 1,
  "group_id": 123,
  "start_time": "2025-07-23T09:00:00+07:00",
  "end_time": "2025-07-23T10:30:00+07:00",
  "room_id": 456,
  "teacher_id": 789,
  "created_at": "2025-07-23T08:00:00+07:00",
  "updated_at": "2025-07-23T08:00:00+07:00"
}
```

### 4. Generate Weekly Schedule

**Endpoint:** `POST /api/v1/schedule/weekly`

Generates a weekly schedule for a specific group within a date range.

**Query Parameters:**
- `groupId` (required): Group ID to generate schedule for
- `startDate` (required): Start date for schedule generation (format: ISO-8601)
- `endDate` (required): End date for schedule generation (format: ISO-8601)

**Example Request:**
```
POST /api/v1/schedule/weekly?groupId=123&startDate=2025-07-23T00:00:00Z&endDate=2025-07-30T00:00:00Z
```

**Response:**
```json
[
  {
    "id": 1,
    "group_id": 123,
    "start_time": "2025-07-23T09:00:00+07:00",
    "end_time": "2025-07-23T10:30:00+07:00",
    "room_id": 456,
    "teacher_id": 789,
    "created_at": "2025-07-23T08:00:00+07:00",
    "updated_at": "2025-07-23T08:00:00+07:00"
  }
  // ... other schedules in the week
]
```

## Common Response Fields

Each schedule object contains the following fields:
- `id`: Unique identifier for the schedule
- `group_id`: ID of the group this schedule belongs to
- `start_time`: Start time of the schedule (ISO-8601 format with timezone)
- `end_time`: End time of the schedule (ISO-8601 format with timezone)
- `room_id`: ID of the room where the class takes place
- `teacher_id`: ID of the teacher assigned to this schedule
- `created_at`: Timestamp when the schedule was created
- `updated_at`: Timestamp when the schedule was last updated

## Error Responses

In case of errors, the API will return appropriate HTTP status codes:

- `400 Bad Request`: Invalid parameters or request format
- `404 Not Found`: Schedule not found
- `500 Internal Server Error`: Server-side error

Error response format:
```json
{
  "status": 404,
  "message": "Schedule not found",
  "timestamp": "2025-07-23T08:00:00+07:00"
}
```
