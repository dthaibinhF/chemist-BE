package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.ScheduleDTO;
import dthaibinhf.project.chemistbe.mapper.ScheduleMapper;
import dthaibinhf.project.chemistbe.model.*;
import dthaibinhf.project.chemistbe.repository.GroupRepository;
import dthaibinhf.project.chemistbe.repository.RoomRepository;
import dthaibinhf.project.chemistbe.repository.ScheduleRepository;
import dthaibinhf.project.chemistbe.repository.TeacherRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.function.Supplier;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final RoomRepository roomRepository;
    // Add at the top of the class or in a separate Constants class
    private static final Set<String> VALID_DELIVERY_MODES = Set.of("ONLINE", "OFFLINE", "HYBRID");
    private static final String ONLINE_DELIVERY_MODE = "ONLINE";

/**
     * Combines a LocalDate with a LocalTime to create an OffsetDateTime using Ho Chi Minh City timezone.
     * This is used to convert GroupSchedule template times (LocalTime) with schedule dates (LocalDate)
     * into Schedule entity times (OffsetDateTime).
     */
    private OffsetDateTime combineDateTime(LocalDate date, LocalTime time) {
        ZoneId hoChiMinhZone = ZoneId.of("Asia/Ho_Chi_Minh");
        return OffsetDateTime.of(date, time, hoChiMinhZone.getRules().getOffset(date.atTime(time)));
    }

    private void validateParameters(Integer groupId, OffsetDateTime startDate, OffsetDateTime endDate) {
        if (groupId != null && groupRepository.findActiveById(groupId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + groupId);
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }
    }

    private boolean matchesFilter(Schedule schedule, Integer groupId,
                                  OffsetDateTime startDate, OffsetDateTime endDate) {
        return (groupId == null || schedule.getGroup().getId().equals(groupId)) &&
                (startDate == null || !schedule.getStartTime().isBefore(startDate)) &&
                (endDate == null || !schedule.getEndTime().isAfter(endDate));
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<ScheduleDTO> getAllSchedulesPageable(Pageable pageable, Integer groupId,
                                                     OffsetDateTime startDate, OffsetDateTime endDate) {
        try {
            log.info("Fetching pageable schedules with filters - groupId: {}, startDate: {}, endDate: {}",
                    groupId, startDate, endDate);
            validateParameters(groupId, startDate, endDate);
            List<Schedule> schedules = scheduleRepository.findAllActivePageable(groupId, startDate, endDate, pageable).getContent();
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

    public ScheduleDTO getScheduleById(Integer id) {
        log.info("Fetching schedule by id: {}", id);
        return executeWithErrorHandling(
                () -> {
                    Schedule schedule = findScheduleOrThrow(id);
                    return scheduleMapper.toDto(schedule);
                },
                "Failed to fetch schedule",
                "Schedule not found with id: " + id
        );
    }

    private Schedule findScheduleOrThrow(Integer id) {
        return scheduleRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Schedule not found: " + id));
    }

    private <T> T executeWithErrorHandling(Supplier<T> operation, String errorMessage, String notFoundMessage) {
        try {
            return operation.get();
        } catch (ResponseStatusException e) {
            log.error(notFoundMessage);
            throw e;
        } catch (Exception e) {
            log.error(errorMessage, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
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

        if (ONLINE_DELIVERY_MODE.equals(deliveryMode) && isEmptyMeetingLink(scheduleDTO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Meeting link required for online delivery");
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

        if (scheduleDTO.getTeacher() != null && scheduleDTO.getTeacher().getId() != null) {
            if (scheduleRepository.existsTeacherConflict(
                    scheduleDTO.getTeacher().getId(),
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

            if (scheduleDTO.getTeacher() != null && scheduleDTO.getTeacher().getId() != null) {
                Teacher teacher = teacherRepository.findActiveById(scheduleDTO.getTeacher().getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Teacher not found: " + scheduleDTO.getTeacher().getId()));
                schedule.setTeacher(teacher);
            } else {
                schedule.setTeacher(null); // Teacher is optional
            }
        } catch (ResponseStatusException e) {
            log.error("Error fetching related entities", e);
            throw e;
        }
    }

    public Set<Schedule> generateWeeklySchedule(Integer groupId, OffsetDateTime startDate, OffsetDateTime endDate) {
        try {
            log.info("Generating weekly schedule for group: {} from {} to {}", groupId, startDate, endDate);
            
            // Step 1: Validate input parameters
            validateScheduleGenerationParams(groupId, startDate, endDate);

            // Step 2: Get the group and validate it exists
            Group group = getActiveGroupById(groupId);

            // Step 3: Check if group has schedule templates
            if (group.getGroupSchedules().isEmpty()) {
                log.warn("Group {} has no schedule templates configured", groupId);
                return new LinkedHashSet<>();
            }

            // Step 4: Check for existing schedules in the date range
            validateNoExistingSchedules(groupId, startDate, endDate);

            // Step 5: Generate new schedules based on templates
            Set<Schedule> newSchedules = createSchedulesFromTemplates(group, startDate, endDate);

            // Step 6: Save generated schedules
            if (!newSchedules.isEmpty()) {
                scheduleRepository.saveAll(newSchedules);
                log.info("Successfully generated {} schedules for group: {}", newSchedules.size(), groupId);
            } else {
                log.info("No schedules could be generated for group: {} (possible conflicts)", groupId);
            }

            return newSchedules;
        } catch (ResponseStatusException e) {
            log.error("Validation error generating weekly schedule for group {}: {}", groupId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error generating weekly schedule for group {}", groupId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate weekly schedule");
        }
    }

    private void validateScheduleGenerationParams(Integer groupId, OffsetDateTime startDate, OffsetDateTime endDate) {
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
        if (startDate.isBefore(OffsetDateTime.now().minusDays(1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date cannot be in the past");
        }
    }
    
    private Group getActiveGroupById(Integer groupId) {
        return groupRepository.findActiveById(groupId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + groupId));
    }

    private void validateNoExistingSchedules(Integer groupId, OffsetDateTime startDate, OffsetDateTime endDate) {
        List<Schedule> existingSchedules = scheduleRepository.findAllActivePageable(
                groupId, startDate, endDate, Pageable.unpaged()).getContent();

        if (!existingSchedules.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Group %d already has %d schedules between %s and %s. Remove existing schedules first.",
                            groupId, existingSchedules.size(), startDate.toLocalDate(), endDate.toLocalDate()));
        }
    }

    private Set<Schedule> createSchedulesFromTemplates(Group group, OffsetDateTime startDate, OffsetDateTime endDate) {
        Set<Schedule> newSchedules = new LinkedHashSet<>();
        List<GroupSchedule> templates = getSortedGroupScheduleTemplates(group);

        for (GroupSchedule template : templates) {
            OffsetDateTime nextMatchingDate = findFirstMatchingDayOfWeek(startDate, endDate, template.getDayOfWeekEnum());
            
            if (nextMatchingDate != null) {
                Schedule newSchedule = createScheduleFromTemplate(group, template, nextMatchingDate);
                
                if (isScheduleValid(newSchedule)) {
                    newSchedules.add(newSchedule);
                } else {
                    log.warn("Skipping schedule for {} on {} due to conflicts", 
                            template.getDayOfWeekEnum(), nextMatchingDate.toLocalDate());
                }
            }
        }

        return newSchedules;
    }

    private List<GroupSchedule> getSortedGroupScheduleTemplates(Group group) {
        return group.getGroupSchedules().stream()
                .sorted(Comparator.comparing(GroupSchedule::getDayOfWeekEnum))
                .toList();
    }

    private OffsetDateTime findFirstMatchingDayOfWeek(OffsetDateTime startDate, OffsetDateTime endDate,
                                                      java.time.DayOfWeek targetDayOfWeek) {
        OffsetDateTime current = startDate;

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

    private Schedule createScheduleFromTemplate(Group group, GroupSchedule template, OffsetDateTime date) {
        LocalDate scheduleDate = date.toLocalDate();
        
        Schedule schedule = Schedule.builder()
                .group(group)
                .startTime(combineDateTime(scheduleDate, template.getStartTime()))
                .endTime(combineDateTime(scheduleDate, template.getEndTime()))
                .deliveryMode("OFFLINE")  // Default delivery mode
                .teacher(null)  // Teacher to be assigned later
                .room(template.getRoom())  // Use room from template
                .meetingLink(null)  // No meeting link for offline mode
                .attendances(null)  // No attendances yet
                .build();

        schedule.setId(null);  // Ensure new record
        return schedule;
    }
}
