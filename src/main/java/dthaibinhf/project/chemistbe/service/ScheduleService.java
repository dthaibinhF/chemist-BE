package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.GroupDTO;
import dthaibinhf.project.chemistbe.dto.ScheduleDTO;
import dthaibinhf.project.chemistbe.mapper.GroupMapper;
import dthaibinhf.project.chemistbe.mapper.ScheduleMapper;
import dthaibinhf.project.chemistbe.model.*;
import dthaibinhf.project.chemistbe.repository.GroupRepository;
import dthaibinhf.project.chemistbe.repository.GroupScheduleRepository;
import dthaibinhf.project.chemistbe.repository.RoomRepository;
import dthaibinhf.project.chemistbe.repository.ScheduleRepository;
import dthaibinhf.project.chemistbe.repository.TeacherRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Supplier;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    // Add at the top of the class or in a separate Constants class
    private static final Set<String> VALID_DELIVERY_MODES = Set.of("ONLINE", "OFFLINE", "HYBRID");
    private static final String ONLINE_DELIVERY_MODE = "ONLINE";
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final GroupRepository groupRepository;
    private final GroupScheduleRepository groupScheduleRepository;
    private final GroupMapper groupMapper;
    private final TeacherRepository teacherRepository;
    private final RoomRepository roomRepository;

    /**
     * Combines a LocalDate with a LocalTime to create an OffsetDateTime using Ho Chi Minh City timezone.
     * This is used to convert GroupSchedule template times (LocalTime) with schedule dates (LocalDate)
     * into Schedule entity times (OffsetDateTime).
     */
    private OffsetDateTime combineDateTime(LocalDate date, LocalTime time) {
        ZoneId hoChiMinhZone = ZoneId.of("Asia/Ho_Chi_Minh");
        return OffsetDateTime.of(date, time, hoChiMinhZone.getRules().getOffset(date.atTime(time)));
    }

    private void validateParameters(Integer groupId, LocalDate startDate, LocalDate endDate) {
        if (groupId != null && groupRepository.findActiveById(groupId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + groupId);
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }
    }


    @Tool(description = "Get all active schedules in the system. Useful for queries about overall schedule information.")
    @Transactional()
    public List<ScheduleDTO> getAllSchedules() {
        try {
            log.info("Fetching all active schedules");
            List<Schedule> schedules = scheduleRepository.findAllActive();
            return schedules.stream()
                    .map(scheduleMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all schedules", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch schedules");
        }
    }

    @Tool(description = "Search schedules with filters: groupId, startDate, endDate. Useful for finding schedules by specific criteria.")
    @Transactional
    public List<ScheduleDTO> getAllSchedulesPageable(Pageable pageable, Integer groupId,
                                                     LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Fetching pageable schedules with filters - groupId: {}, startDate: {}, endDate: {}",
                    groupId, startDate, endDate);
            validateParameters(groupId, startDate, endDate);

            OffsetDateTime offsetStartDate = (startDate != null) ? startDate.atTime(0, 0).atOffset(ZoneOffset.ofHours(7)) : null;
            OffsetDateTime offsetEndDate = (endDate != null) ? endDate.atTime(23, 59).atOffset(ZoneOffset.ofHours(7)) : null;
            List<Schedule> schedules = scheduleRepository.findAllActivePageable(groupId, offsetStartDate, offsetEndDate, pageable).getContent();
            return schedules.stream()
                    .map(scheduleMapper::toDto)
                    .collect(Collectors.toList());
        } catch (ResponseStatusException e) {
            log.error("Validation error in getAllSchedulesPageable", e);
            throw e;
        } catch (Exception e) {
            log.error("Error fetching pageable schedules", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch pageable schedules");
        }
    }

    @Tool(description = "Get detailed information about a specific schedule by ID.")
    public ScheduleDTO getScheduleById(Integer id) {
        log.info("Fetching schedule by id: {}", id);
        return executeWithErrorHandling(
                () -> {
                    Schedule schedule = findScheduleOrThrow(id);
                    return scheduleMapper.toDto(schedule);
                },
                "Schedule not found with id: " + id
        );
    }

    private Schedule findScheduleOrThrow(Integer id) {
        return scheduleRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Schedule not found: " + id));
    }

    private <T> T executeWithErrorHandling(Supplier<T> operation, String notFoundMessage) {
        try {
            return operation.get();
        } catch (ResponseStatusException e) {
            log.error(notFoundMessage);
            throw e;
        } catch (Exception e) {
            log.error("Failed to fetch schedule", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch schedule");
        }
    }

    public ScheduleDTO createSchedule(@Valid ScheduleDTO scheduleDTO) {
        try {
            log.info("Creating new schedule: {}", scheduleDTO);
            validateSchedule(scheduleDTO);
            checkScheduleConflicts(scheduleDTO);
            Schedule schedule = scheduleMapper.toEntity(scheduleDTO);
            schedule.setId(null);
            setRelatedEntities(schedule, scheduleDTO);
            Schedule savedSchedule = scheduleRepository.save(schedule);
            log.info("Schedule created successfully with id: {}", savedSchedule.getId());
            return scheduleMapper.toDto(savedSchedule);
        } catch (ResponseStatusException e) {
            log.error("Validation error while creating schedule", e);
            throw e;
        } catch (Exception e) {
            log.error("Error creating schedule", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create schedule");
        }
    }

    public ScheduleDTO updateSchedule(Integer id, @Valid ScheduleDTO scheduleDTO) {
        try {
            log.info("Updating schedule with id: {}", id);
            Schedule schedule = scheduleRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found: " + id));
            validateSchedule(scheduleDTO);
            checkScheduleConflicts(scheduleDTO, id);
            scheduleMapper.partialUpdate(scheduleDTO, schedule);
            setRelatedEntities(schedule, scheduleDTO);
            Schedule updatedSchedule = scheduleRepository.save(schedule);
            log.info("Schedule updated successfully with id: {}", id);
            return scheduleMapper.toDto(updatedSchedule);
        } catch (ResponseStatusException e) {
            log.error("Validation error while updating schedule with id: {}", id, e);
            throw e;
        } catch (Exception e) {
            log.error("Error updating schedule with id: {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update schedule");
        }
    }

    public void deleteSchedule(Integer id) {
        try {
            log.info("Soft deleting schedule with id: {}", id);
            Schedule schedule = scheduleRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found: " + id));
            schedule.softDelete();
            scheduleRepository.save(schedule);
            log.info("Schedule with id: {} successfully deleted", id);
        } catch (ResponseStatusException e) {
            log.error("Schedule not found with id: {}", id);
            throw e;
        } catch (Exception e) {
            log.error("Error deleting schedule with id: {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete schedule");
        }
    }

    private void validateSchedule(ScheduleDTO scheduleDTO) {
        validateTimeOrder(scheduleDTO);
        validateDeliveryMode(scheduleDTO);
        validateRequiredFields(scheduleDTO);
    }

    private void validateTimeOrder(ScheduleDTO scheduleDTO) {
        if (scheduleDTO.getStartTime().isAfter(scheduleDTO.getEndTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be before end time");
        }
    }

    private void validateDeliveryMode(ScheduleDTO scheduleDTO) {
        String deliveryMode = scheduleDTO.getDeliveryMode();

        if (!VALID_DELIVERY_MODES.contains(deliveryMode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid delivery mode");
        }
    }

    private boolean isEmptyMeetingLink(ScheduleDTO scheduleDTO) {
        return scheduleDTO.getMeetingLink() == null || scheduleDTO.getMeetingLink().trim().isEmpty();
    }

    private void validateRequiredFields(ScheduleDTO scheduleDTO) {
        if (scheduleDTO.getGroupId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group ID is required");
        }

        if (scheduleDTO.getRoom() == null || scheduleDTO.getRoom().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room is required");
        }
    }

    private void checkScheduleConflicts(ScheduleDTO scheduleDTO, Integer... excludeId) {
        Integer excludeIdParam = excludeId.length > 0 ? excludeId[0] : null;

        if (scheduleRepository.existsRoomConflict(
                scheduleDTO.getRoom().getId(),
                scheduleDTO.getStartTime(),
                scheduleDTO.getEndTime(),
                excludeIdParam)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is already booked for the specified time");
        }

        if (scheduleDTO.getTeacherId() != null) {
            if (scheduleRepository.existsTeacherConflict(
                    scheduleDTO.getTeacherId(),
                    scheduleDTO.getStartTime(),
                    scheduleDTO.getEndTime(),
                    excludeIdParam)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Teacher is already assigned for the specified time");
            }
        }
    }

    private void setRelatedEntities(Schedule schedule, ScheduleDTO scheduleDTO) {
        try {
            Group group = groupRepository.findActiveById(scheduleDTO.getGroupId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Group not found: " + scheduleDTO.getGroupId()));
            schedule.setGroup(group);

            Room room = roomRepository.findActiveById(scheduleDTO.getRoom().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Room not found: " + scheduleDTO.getRoom().getId()));
            schedule.setRoom(room);

            if (scheduleDTO.getTeacherId() != null && scheduleDTO.getTeacherId() > 0) {
                Teacher teacher = teacherRepository.findActiveById(scheduleDTO.getTeacherId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Teacher not found: " + scheduleDTO.getTeacherId()));
                schedule.setTeacher(teacher);
            } else {
                schedule.setTeacher(null); // Teacher is optional
            }
        } catch (ResponseStatusException e) {
            log.error("Error fetching related entities", e);
            throw e;
        }
    }

    public Set<ScheduleDTO> generateWeeklySchedule(Integer groupId, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Generating weekly schedule for group: {} from {} to {}", groupId, startDate, endDate);

            // Step 1: Validate input parameters
            validateScheduleGenerationParams(groupId, startDate, endDate);

            // Step 2: Get the group and validate it exists
            Group group = getActiveGroupById(groupId);

            // Step 3: Check if a group has schedule templates
            if (group.getGroupSchedules().isEmpty()) {
                log.warn("Group {} has no schedule templates configured", groupId);
                return new LinkedHashSet<>();
            }

            // Step 4: Get existing schedule dates in the range
            Set<LocalDate> existingScheduleDates = getExistingScheduleDates(groupId, startDate, endDate);

            // Step 5: Generate new schedules based on templates, skipping existing dates
            Set<Schedule> newSchedules = createSchedulesFromTemplates(group, startDate, endDate, existingScheduleDates);

            // Step 6: Save generated schedules
            if (!newSchedules.isEmpty()) {
                scheduleRepository.saveAll(newSchedules);
                log.info("Successfully generated {} new schedules for group: {} (skipped {} existing dates)", 
                        newSchedules.size(), groupId, existingScheduleDates.size());
            } else {
                if (existingScheduleDates.isEmpty()) {
                    log.info("No schedules could be generated for group: {} (possible conflicts or no matching days)", groupId);
                } else {
                    log.info("No new schedules generated for group: {} - all {} dates in range already have schedules", 
                            groupId, existingScheduleDates.size());
                }
            }

            return newSchedules.stream().map(scheduleMapper::toDto).collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (ResponseStatusException e) {
            log.error("Validation error generating weekly schedule for group {}: {}", groupId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error generating weekly schedule for group {}", groupId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate weekly schedule");
        }
    }

    private void validateScheduleGenerationParams(Integer groupId, LocalDate startDate, LocalDate endDate) {
        if (groupId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group ID is required");
        }
        if (startDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date is required");
        }
        if (endDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date is required");
        }
        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }
    }

    private Group getActiveGroupById(Integer groupId) {
        return groupRepository.findActiveById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + groupId));
    }

    private Set<LocalDate> getExistingScheduleDates(Integer groupId, LocalDate startDate, LocalDate endDate) {
        OffsetDateTime offsetStartDate = startDate.atStartOfDay()
                .atOffset(ZoneOffset.ofHours(7));

        OffsetDateTime offsetEndDate = endDate.atTime(23, 59)
                .atOffset(ZoneOffset.ofHours(7)); // Result: 2025-07-23T23:59:00+07:00
        List<Schedule> existingSchedules = scheduleRepository.findAllActivePageable(
                groupId, offsetStartDate, offsetEndDate, Pageable.unpaged()).getContent();

        Set<LocalDate> existingDates = existingSchedules.stream()
                .map(schedule -> schedule.getStartTime().toLocalDate())
                .collect(Collectors.toSet());
        
        log.info("Found {} existing schedule dates for group {} between {} and {}: {}", 
                existingDates.size(), groupId, startDate, endDate, existingDates);
        
        return existingDates;
    }

    private Set<Schedule> createSchedulesFromTemplates(Group group, LocalDate startDate, LocalDate endDate, Set<LocalDate> existingScheduleDates) {
        Set<Schedule> newSchedules = new LinkedHashSet<>();
        List<GroupSchedule> templates = getSortedGroupScheduleTemplates(group);

        for (GroupSchedule template : templates) {
            LocalDate nextMatchingDate = findFirstMatchingDayOfWeek(startDate, endDate, template.getDayOfWeekEnum());

            if (nextMatchingDate != null) {
                // Skip if this date already has a schedule for this group
                if (existingScheduleDates.contains(nextMatchingDate)) {
                    log.debug("Skipping schedule creation for {} on {} - schedule already exists", 
                            template.getDayOfWeekEnum(), nextMatchingDate);
                    continue;
                }

                //TODO: this method should use for create a list of schedules from start date to end date, not just one
                // Create a new schedule based on the template
                //then add it to the newSchedules set
                log.debug("Creating new schedule for {} on {}", template.getDayOfWeekEnum(), nextMatchingDate);
                Schedule newSchedule = createScheduleFromTemplate(group, template, nextMatchingDate);

                if (isScheduleValid(newSchedule)) {
                    newSchedules.add(newSchedule);
                    log.debug("Created new schedule for {} on {}", 
                            template.getDayOfWeekEnum(), nextMatchingDate);
                } else {
                    log.warn("Skipping schedule for {} on {} due to conflicts",
                            template.getDayOfWeekEnum(), nextMatchingDate);
                }
            }
        }

        return newSchedules;
    }

    /**
     * Retrieves a list of group schedule templates sorted by the day of the week.
     *
     * @param group The group object containing the unsorted group schedules.
     * @return A sorted list of GroupSchedule objects based on the day of the week.
     */
    private List<GroupSchedule> getSortedGroupScheduleTemplates(Group group) {
        return group.getGroupSchedules().stream()
                .sorted(Comparator.comparing(GroupSchedule::getDayOfWeekEnum))
                .toList();
    }

    private LocalDate findFirstMatchingDayOfWeek(LocalDate startDate,
                                                      LocalDate endDate,
                                                      DayOfWeek targetDayOfWeek) {
        LocalDate current = startDate;

        while (current.isBefore(endDate)) {
            if (current.getDayOfWeek().equals(targetDayOfWeek)) {
                return current;
            }
            current = current.plusDays(1);
        }

        return null; // No matching day found in range
    }

    private boolean isScheduleValid(Schedule schedule) {
        try {
            checkScheduleConflicts(scheduleMapper.toDto(schedule));
            return true;
        } catch (ResponseStatusException e) {
            return false;
        }
    }

    private Schedule createScheduleFromTemplate(Group group, GroupSchedule template, LocalDate date) {

        Schedule schedule = Schedule.builder()
                .group(group)
                .startTime(combineDateTime(date, template.getStartTime()))
                .endTime(combineDateTime(date, template.getEndTime()))
                .deliveryMode("OFFLINE")  // Default delivery mode
                .teacher(null)  // Teacher to be assigned later
                .room(template.getRoom())  // Use room from a template
                .meetingLink(null)  // No meeting link for offline mode
                .attendances(null)  // No attendances yet
                .build();

        schedule.setId(null);  // Ensure a new record
        return schedule;
    }

    /**
     * Generate schedules for multiple groups in a single operation
     */
    @Transactional
    public List<Set<ScheduleDTO>> generateBulkWeeklySchedules(List<Integer> groupIds, LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Generating bulk weekly schedules for {} groups from {} to {}", groupIds.size(), startDate, endDate);

            return groupIds.stream()
                    .map(groupId -> {
                        try {
                            return generateWeeklySchedule(groupId, startDate, endDate);
                        } catch (Exception e) {
                            log.error("Failed to generate schedules for group: {}", groupId, e);
                            return new LinkedHashSet<ScheduleDTO>();
                        }
                    })
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error in bulk weekly schedule generation", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate bulk schedules");
        }
    }

    /**
     * Generate schedules for all active groups
     */
    @Transactional
    public List<Set<ScheduleDTO>> generateSchedulesForAllActiveGroups(LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Generating schedules for all active groups from {} to {}", startDate, endDate);

            List<Group> activeGroups = groupRepository.findAllActiveGroups();
            List<Integer> groupIds = activeGroups.stream()
                    .map(Group::getId)
                    .collect(Collectors.toList());

            return generateBulkWeeklySchedules(groupIds, startDate, endDate);

        } catch (Exception e) {
            log.error("Error generating schedules for all active groups", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate schedules for all groups");
        }
    }

    /*
     * TODO: Temporarily commented out - missing ScheduleUpdateRequest/Response and UpdateMode DTOs
     *
     * Update schedule with option for single occurrence or all future occurrences
     *
    @Transactional
    public ScheduleUpdateResponse updateScheduleWithMode(Integer scheduleId, ScheduleUpdateRequest request) {
        // Implementation commented out due to missing DTOs
    }

    private ScheduleUpdateResponse updateSingleScheduleOccurrence(Schedule schedule, ScheduleUpdateRequest request) {
        // Implementation commented out due to missing DTOs
    }

    private ScheduleUpdateResponse updateScheduleAndFutureOccurrences(Schedule currentSchedule, ScheduleUpdateRequest request) {
        // Implementation commented out due to missing DTOs
    }

    private List<Schedule> findFutureSchedulesWithSamePattern(Schedule referenceSchedule) {
        // Implementation commented out due to missing DTOs
    }

    private void applyScheduleUpdates(Schedule schedule, ScheduleUpdateRequest request) {
        // Implementation commented out due to missing DTOs
    }
     */

    /**
     * Get count of future schedules that the update would affect
     */
    public int getFutureSchedulesCount(Integer scheduleId) {
        try {
            Schedule schedule = findScheduleOrThrow(scheduleId);
            // Simple implementation using existing repository method
            List<Schedule> futureSchedules = scheduleRepository.findActiveSchedulesByGroupIdAfterDate(
                    schedule.getGroup().getId(),
                    schedule.getStartTime()
            );

            // Filter by same day of week
            DayOfWeek dayOfWeek = schedule.getStartTime().getDayOfWeek();
            return (int) futureSchedules.stream()
                    .filter(s -> s.getStartTime().getDayOfWeek().equals(dayOfWeek))
                    .filter(s -> !s.getId().equals(scheduleId)) // Exclude current schedule
                    .count();
        } catch (Exception e) {
            log.error("Error getting future schedules count for schedule {}", scheduleId, e);
            return 0;
        }
    }

    private void validateDeliveryModeString(String deliveryMode) {
        if (!VALID_DELIVERY_MODES.contains(deliveryMode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid delivery mode: " + deliveryMode);
        }
    }

    @Tool(description = "Find groups that have classes matching specific day pattern and time range. Use this for queries like 'groups studying on Tuesday-Thursday-Saturday from 17:20-19:00'. Days should be in English (MONDAY, TUESDAY, etc.), times in HH:mm format.")
    @Transactional(readOnly = true)
    public List<GroupDTO> findGroupsBySchedulePattern(List<String> dayNames, String startTime, String endTime) {
        try {
            log.info("Searching for groups with schedule pattern - days: {}, time: {}-{}", dayNames, startTime, endTime);

            if (dayNames == null || dayNames.isEmpty()) {
                return List.of();
            }

            // Convert string times to LocalTime
            LocalTime startTimeLocal = LocalTime.parse(startTime);
            LocalTime endTimeLocal = LocalTime.parse(endTime);

            // Convert day names to DayOfWeek enum
            Set<DayOfWeek> targetDays = dayNames.stream()
                    .map(String::toUpperCase)
                    .map(DayOfWeek::valueOf)
                    .collect(Collectors.toSet());

            // Get all active groups
            List<Group> allGroups = groupRepository.findAllActiveGroups();

            // Filter groups that match the schedule pattern
            List<Group> matchingGroups = allGroups.stream()
                    .filter(group -> hasMatchingSchedulePattern(group, targetDays, startTimeLocal, endTimeLocal))
                    .toList();

            log.info("Found {} groups matching the pattern", matchingGroups.size());
            return matchingGroups.stream()
                    .map(groupMapper::toDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error searching for groups by schedule pattern", e);
            return List.of();
        }
    }

    private boolean hasMatchingSchedulePattern(Group group, Set<DayOfWeek> targetDays,
                                               LocalTime startTime, LocalTime endTime) {
        Set<GroupSchedule> groupSchedules = group.getGroupSchedules();

        if (groupSchedules.isEmpty()) {
            return false;
        }

        // Get the days this group has classes
        Set<DayOfWeek> groupDays = groupSchedules.stream()
                .map(GroupSchedule::getDayOfWeekEnum)
                .collect(Collectors.toSet());

        // Check if group days match target days (must be exactly the same or subset)
        if (!targetDays.equals(groupDays)) {
            return false;
        }

        // Check if any schedule has a matching time range
        return groupSchedules.stream()
                .anyMatch(schedule ->
                    schedule.getStartTime().equals(startTime) &&
                    schedule.getEndTime().equals(endTime)
                );
    }

    @Tool(description = "Get schedules for a specific date range. Useful for queries about schedules in a particular week or month.")
    @Transactional(readOnly = true)
    public List<ScheduleDTO> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            log.info("Getting schedules from {} to {}", startDate, endDate);

            OffsetDateTime offsetStartDate = startDate.atStartOfDay().atOffset(ZoneOffset.ofHours(7));
            OffsetDateTime offsetEndDate = endDate.atTime(23, 59).atOffset(ZoneOffset.ofHours(7));

            List<Schedule> schedules = scheduleRepository.findAllActivePageable(
                null, offsetStartDate, offsetEndDate, Pageable.unpaged()).getContent();

            return schedules.stream()
                    .map(scheduleMapper::toDto)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error getting schedules by date range", e);
            return List.of();
        }
    }
}
