# Schedule Service Documentation

## Overview
The Schedule Service provides functionality for managing schedules in the Chemist-BE application. It handles operations related to creating, retrieving, updating, and deleting schedules, as well as generating weekly schedules based on group schedule templates.

## Dependencies
The Schedule Service depends on the following components:
- `ScheduleRepository`: For database operations related to schedules
- `ScheduleMapper`: For mapping between Schedule entities and DTOs
- `GroupRepository`: For retrieving group information
- `TeacherRepository`: For retrieving teacher information
- `RoomRepository`: For retrieving room information

## Methods

### getAllSchedules
```
public List<ScheduleDTO> getAllSchedules(Pageable pageable, Integer groupId, 
                                        OffsetDateTime startDate, OffsetDateTime endDate)
```
Retrieves all active schedules from the database. Currently, it does not use the provided parameters for filtering.

**Parameters:**
- `pageable`: Pagination information
- `groupId`: Optional filter by group ID
- `startDate`: Optional filter by start date
- `endDate`: Optional filter by end date

**Returns:**
A list of ScheduleDTO objects representing all active schedules.

### getScheduleById
```
public ScheduleDTO getScheduleById(Integer id)
```
Retrieves a specific schedule by its ID.

**Parameters:**
- `id`: The ID of the schedule to retrieve

**Returns:**
A ScheduleDTO object representing the requested schedule.

**Throws:**
- `ResponseStatusException` with HTTP status NOT_FOUND if the schedule does not exist or is not active.

### createSchedule
```
@Transactional
public ScheduleDTO createSchedule(@Valid ScheduleDTO scheduleDTO)
```
Creates a new schedule based on the provided DTO.

**Parameters:**
- `scheduleDTO`: The DTO containing the schedule information

**Returns:**
A ScheduleDTO object representing the newly created schedule.

**Throws:**
- `ResponseStatusException` with various HTTP statuses for validation errors.

### updateSchedule
```
@Transactional
public ScheduleDTO updateSchedule(Integer id, @Valid ScheduleDTO scheduleDTO)
```
Updates an existing schedule with the provided information.

**Parameters:**
- `id`: The ID of the schedule to update
- `scheduleDTO`: The DTO containing the updated schedule information

**Returns:**
A ScheduleDTO object representing the updated schedule.

**Throws:**
- `ResponseStatusException` with HTTP status NOT_FOUND if the schedule does not exist.
- `ResponseStatusException` with various HTTP statuses for validation errors.

### deleteSchedule
```
@Transactional
public void deleteSchedule(Integer id)
```
Soft deletes a schedule by marking it as deleted.

**Parameters:**
- `id`: The ID of the schedule to delete

**Throws:**
- `ResponseStatusException` with HTTP status NOT_FOUND if the schedule does not exist.

### generateWeeklySchedule
```
public Set<Schedule> generateWeeklySchedule(Integer groupId, OffsetDateTime startDate, 
                                           OffsetDateTime endDate)
```
Generates weekly schedules for a group based on its group schedule templates.

**Parameters:**
- `groupId`: The ID of the group for which to generate schedules
- `startDate`: The start date for the schedule generation period
- `endDate`: The end date for the schedule generation period

**Returns:**
A set of Schedule objects representing the generated schedules.

**Throws:**
- `ResponseStatusException` with HTTP status NOT_FOUND if the group does not exist.
- `ResponseStatusException` with HTTP status BAD_REQUEST for invalid date parameters.

## Helper Methods

### validateSchedule
```
private void validateSchedule(ScheduleDTO scheduleDTO)
```
Validates a schedule DTO to ensure it meets all requirements.

**Parameters:**
- `scheduleDTO`: The DTO to validate

**Throws:**
- `ResponseStatusException` with HTTP status BAD_REQUEST for various validation errors.

### checkScheduleConflicts
```
private void checkScheduleConflicts(ScheduleDTO scheduleDTO, Integer... excludeId)
```
Checks for conflicts with existing schedules for the same room or teacher.

**Parameters:**
- `scheduleDTO`: The DTO to check for conflicts
- `excludeId`: Optional ID to exclude from conflict checking (used during updates)

**Throws:**
- `ResponseStatusException` with HTTP status CONFLICT if conflicts are found.

### setRelatedEntities
```
private void setRelatedEntities(Schedule schedule, ScheduleDTO scheduleDTO)
```
Sets the related entities (group, room, teacher) on a schedule based on the DTO.

**Parameters:**
- `schedule`: The schedule entity to update
- `scheduleDTO`: The DTO containing the related entity information

**Throws:**
- `ResponseStatusException` with HTTP status NOT_FOUND if any related entity does not exist.