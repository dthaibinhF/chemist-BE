# Schedule Controller Documentation

## Overview
The Schedule Controller provides RESTful API endpoints for managing schedules in the Chemist-BE application. It handles HTTP requests related to creating, retrieving, updating, and deleting schedules, as well as generating weekly schedules based on group schedule templates.

## Base URL
All endpoints are prefixed with `/api/v1/schedule`.

## Dependencies
The Schedule Controller depends on the following components:
- `ScheduleService`: For handling business logic related to schedules

## Endpoints

### Get All Schedules
```
GET /api/v1/schedule
```
Retrieves all active schedules, with optional filtering by group, start date, and end date.

**Query Parameters:**
- `groupId` (optional): Filter schedules by group ID
- `startDate` (optional): Filter schedules by start date
- `endDate` (optional): Filter schedules by end date
- Pagination parameters (e.g., `page`, `size`, `sort`)

**Response:**
- `200 OK`: Returns a list of ScheduleDTO objects representing the schedules

### Get Schedule by ID
```
GET /api/v1/schedule/{id}
```
Retrieves a specific schedule by its ID.

**Path Parameters:**
- `id`: The ID of the schedule to retrieve

**Response:**
- `200 OK`: Returns a ScheduleDTO object representing the requested schedule
- `404 Not Found`: If the schedule does not exist or is not active

### Create Schedule
```
POST /api/v1/schedule
```
Creates a new schedule based on the provided information.

**Request Body:**
- ScheduleDTO object containing the schedule information

**Response:**
- `200 OK`: Returns a ScheduleDTO object representing the newly created schedule
- `400 Bad Request`: If the request body is invalid or validation fails
- `409 Conflict`: If there are conflicts with existing schedules

### Update Schedule
```
PUT /api/v1/schedule/{id}
```
Updates an existing schedule with the provided information.

**Path Parameters:**
- `id`: The ID of the schedule to update

**Request Body:**
- ScheduleDTO object containing the updated schedule information

**Response:**
- `200 OK`: Returns a ScheduleDTO object representing the updated schedule
- `400 Bad Request`: If the request body is invalid or validation fails
- `404 Not Found`: If the schedule does not exist
- `409 Conflict`: If there are conflicts with existing schedules

### Delete Schedule
```
DELETE /api/v1/schedule/{id}
```
Soft deletes a schedule by marking it as deleted.

**Path Parameters:**
- `id`: The ID of the schedule to delete

**Response:**
- `204 No Content`: If the schedule was successfully deleted
- `404 Not Found`: If the schedule does not exist

### Generate Weekly Schedule
```
POST /api/v1/schedule/weekly
```
Generates weekly schedules for a group based on its group schedule templates.

**Query Parameters:**
- `groupId`: The ID of the group for which to generate schedules
- `startDate`: The start date for the schedule generation period
- `endDate`: The end date for the schedule generation period

**Response:**
- `200 OK`: If the schedules were successfully generated
- `400 Bad Request`: If the request parameters are invalid
- `404 Not Found`: If the group does not exist
- `409 Conflict`: If there are conflicts with existing schedules

## Data Structures

### ScheduleDTO
The ScheduleDTO is used for transferring schedule data between the client and the server. It contains the following fields:

- `id`: The unique identifier of the schedule
- `group_id`: The ID of the group associated with this schedule
- `group_name`: The name of the group associated with this schedule
- `startTime`: The start time of the schedule
- `endTime`: The end time of the schedule
- `deliveryMode`: The mode of delivery for this schedule (e.g., "ONLINE", "OFFLINE", "HYBRID")
- `meetingLink`: The meeting link for online sessions (required for online delivery)
- `attendances`: The list of attendance records associated with this schedule
- `teacher`: The teacher assigned to this schedule (optional)
- `room`: The room where this schedule takes place