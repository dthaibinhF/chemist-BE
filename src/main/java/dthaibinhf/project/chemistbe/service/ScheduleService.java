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

import java.time.OffsetDateTime;
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
            validateGenerateWeeklyScheduleParams(groupId, startDate, endDate);

            Group group = groupRepository.findActiveById(groupId).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + groupId)
            );

            checkForExistingSchedules(groupId, startDate, endDate);

            Set<Schedule> schedules = group.getGroupSchedules().isEmpty() ?
                    new LinkedHashSet<>() : new LinkedHashSet<>(group.getSchedules());

            generateSchedulesFromTemplate(group, startDate, endDate, schedules);

            if (!schedules.isEmpty()) {
                schedules.forEach(schedule -> {
                    schedule.setId(null);
                    setRelatedEntities(schedule, scheduleMapper.toDto(schedule));
                });
                scheduleRepository.saveAll(schedules);
                log.info("Generated {} schedules for group: {}", schedules.size(), groupId);
            } else {
                log.info("No schedules generated for group: {}", groupId);
            }

            return schedules;
        } catch (ResponseStatusException e) {
            log.error("Error generating weekly schedule", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error generating weekly schedule", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate weekly schedule");
        }
    }

    private void validateGenerateWeeklyScheduleParams(Integer groupId, OffsetDateTime startDate, OffsetDateTime endDate) {
        if (groupId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group ID is required");
        }

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        if (startDate.isBefore(OffsetDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be in the future");
        }
    }

    private void checkForExistingSchedules(Integer groupId, OffsetDateTime startDate, OffsetDateTime endDate) {
        List<Schedule> existingSchedules = scheduleRepository.findAllActivePageable(
                groupId, startDate, endDate, Pageable.unpaged()).getContent();

        if (!existingSchedules.isEmpty()) {
            existingSchedules.forEach(schedule -> {
                if (schedule.getEndTime().getDayOfMonth() == endDate.getDayOfMonth() &&
                        schedule.getEndTime().getMonthValue() == endDate.getMonthValue() &&
                        schedule.getEndTime().getYear() == endDate.getYear()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Group already has schedules between " + startDate + " and " + endDate);
                }
            });
        }
    }

    private void generateSchedulesFromTemplate(Group group, OffsetDateTime startDate,
                                               OffsetDateTime endDate, Set<Schedule> schedules) {
        List<GroupSchedule> sortedSchedules = getSortedGroupSchedules(group);

        for (GroupSchedule groupSchedule : sortedSchedules) {
            OffsetDateTime date = findNextMatchingDay(startDate, endDate, groupSchedule.getDayOfWeekEnum());

            if (date != null) {
                tryAddScheduleForDate(group, groupSchedule, date, schedules);
            }
        }
    }

    private List<GroupSchedule> getSortedGroupSchedules(Group group) {
        return group.getGroupSchedules().stream()
                .sorted(Comparator.comparing(GroupSchedule::getDayOfWeekEnum))
                .toList();
    }

    private OffsetDateTime findNextMatchingDay(OffsetDateTime startDate, OffsetDateTime endDate,
                                               java.time.DayOfWeek targetDay) {
        OffsetDateTime current = startDate;

        while (current.isBefore(endDate)) {
            if (current.getDayOfWeek().equals(targetDay)) {
                return current;
            }
            current = current.plusDays(1);
        }

        return null; // No matching day found before endDate
    }

    private void tryAddScheduleForDate(Group group, GroupSchedule groupSchedule,
                                       OffsetDateTime date, Set<Schedule> schedules) {
        Schedule schedule = createScheduleFromTemplate(group, groupSchedule, date);

        try {
            checkScheduleConflicts(scheduleMapper.toDto(schedule));
            schedules.add(schedule);
        } catch (ResponseStatusException e) {
            log.warn("Conflict detected for schedule on date: {} - {}", date, e.getMessage());
        }
    }

    private Schedule createScheduleFromTemplate(Group group, GroupSchedule template, OffsetDateTime date) {
        return Schedule.builder()
                .group(group)
                .startTime(date.withHour(template.getStartTime().getHour())
                        .withMinute(template.getStartTime().getMinute())
                        .withSecond(template.getStartTime().getSecond()))
                .endTime(date.withHour(template.getEndTime().getHour())
                        .withMinute(template.getEndTime().getMinute())
                        .withSecond(template.getEndTime().getSecond()))
                .deliveryMode("OFFLINE")
                .attendances(null)
                .teacher(null)
                .room(template.getRoom())
                .meetingLink(null)
                .build();
    }
}