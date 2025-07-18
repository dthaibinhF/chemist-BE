# Entities in Chemist-BE Project

This document provides an overview of the entities in the Chemist-BE project and their relationships.

## Base Entity Structure

All entities in the system extend the `BaseEntity` class, which provides the following common fields:

- `id`: Integer primary key with auto-increment
- `createdAt`: Timestamp when the entity was created
- `updatedAt`: Timestamp when the entity was last updated
- `endAt`: Timestamp when the entity was soft-deleted

The `BaseEntity` also provides lifecycle methods for automatically setting these fields:
- `@PrePersist`: Sets `createdAt` to the current time when an entity is created
- `@PreUpdate`: Sets `updatedAt` to the current time when an entity is updated
- `softDelete()`: Sets `endAt` to the current time to implement soft deletion

## Entity List

Based on the project structure, the following entities have been identified:

1. **AcademicYear**: Represents an academic year in the system
2. **Account**: Represents a user account in the system, implements Spring Security's UserDetails
3. **Attendance**: Tracks student attendance
4. **Exam**: Represents an examination
5. **Fee**: Represents a fee or payment requirement
6. **Grade**: Represents a grade level
7. **Group**: Represents a student group or class
8. **GroupSchedule**: Represents the schedule for a group
9. **GroupSession**: Represents a session for a group
10. **PaymentDetail**: Contains details about payments made
11. **Role**: Represents a user role for authorization
12. **Room**: Represents a physical room or location
13. **Schedule**: Represents a schedule or timetable
14. **School**: Represents a school in the system
15. **SchoolClass**: Represents a class within a school
16. **Score**: Represents a student's score or grade
17. **Student**: Represents a student in the system
18. **StudentDetail**: Contains additional details about a student
19. **Teacher**: Represents a teacher in the system
20. **TeacherDetail**: Contains additional details about a teacher

## Key Relationships

This section will be updated as more information about entity relationships is gathered.

## Entity Details

### PaymentDetail

The PaymentDetail entity has the following structure:
- Relationships:
  - Many-to-One with Fee
  - Many-to-One with Student
- Fields:
  - payMethod: String (payment method)
  - amount: BigDecimal (payment amount)
  - description: String (payment description)
  - haveDiscount: BigDecimal (discount amount)

### Student

The Student entity represents a student in the system. More details will be added as they are discovered.

### Teacher

The Teacher entity represents a teacher in the system. More details will be added as they are discovered.

### Schedule

The Schedule entity represents a specific scheduled session in the system. It has the following structure:
- Relationships:
  - Many-to-One with Group (required)
  - Many-to-One with Teacher (optional)
  - Many-to-One with Room (required)
  - One-to-Many with Attendance
- Fields:
  - startTime: OffsetDateTime (required) - The start time of the schedule
  - endTime: OffsetDateTime (required) - The end time of the schedule
  - deliveryMode: String (required) - The mode of delivery (ONLINE, OFFLINE, HYBRID)
  - meetingLink: String (optional) - The meeting link for online sessions

The Schedule entity is used to track when and where a group meets, including which teacher is assigned and which room is used. It supports different delivery modes (online, offline, hybrid) and includes a meeting link for online sessions.

### GroupSchedule

The GroupSchedule entity represents a template for recurring schedules for a group. It has the following structure:
- Relationships:
  - Many-to-One with Group (required)
  - Many-to-One with Room (optional)
- Fields:
  - dayOfWeek: String (required) - The day of the week (MONDAY, TUESDAY, etc.)
  - startTime: OffsetDateTime (required) - The start time of the schedule
  - endTime: OffsetDateTime (required) - The end time of the schedule

The GroupSchedule entity is used to define recurring schedules for a group on specific days of the week. It serves as a template for generating actual Schedule instances. The dayOfWeek field is stored as a string but can be converted to a DayOfWeek enum using the getDayOfWeekEnum() method.

## Notes

This document will be updated as more information about the entities and their relationships is gathered.
