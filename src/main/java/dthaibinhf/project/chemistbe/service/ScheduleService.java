package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.ScheduleDTO;
import dthaibinhf.project.chemistbe.mapper.ScheduleMapper;
import dthaibinhf.project.chemistbe.model.*;
import dthaibinhf.project.chemistbe.repository.GroupRepository;
import dthaibinhf.project.chemistbe.repository.RoomRepository;
import dthaibinhf.project.chemistbe.repository.ScheduleRepository;
import dthaibinhf.project.chemistbe.repository.TeacherRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
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

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ScheduleService {

    ScheduleRepository scheduleRepository;
    ScheduleMapper scheduleMapper;
    GroupRepository groupRepository;
    TeacherRepository teacherRepository;
    RoomRepository roomRepository;


    private void validateParameters(Integer groupId, OffsetDateTime startDate, OffsetDateTime endDate) {
        if (groupId != null && groupRepository.findActiveById(groupId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + groupId);
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
    }

    private boolean matchesFilter(Schedule schedule, Integer groupId,
                                  OffsetDateTime startDate, OffsetDateTime endDate) {
        return (groupId == null || schedule.getGroup().getId().equals(groupId)) &&
                (startDate == null || !schedule.getStartTime().isBefore(startDate)) &&
                (endDate == null || !schedule.getEndTime().isAfter(endDate));
    }

    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAllActive();
        return schedules.stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ScheduleDTO> getAllSchedulesPageable(Pageable pageable, Integer groupId, OffsetDateTime startDate, OffsetDateTime endDate) {
        validateParameters(groupId, startDate, endDate);
        List<Schedule> schedules = scheduleRepository.findAllActivePageable(groupId, startDate, endDate, pageable).getContent();
        return schedules.stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    public ScheduleDTO getScheduleById(Integer id) {
        Schedule schedule = scheduleRepository.findActiveById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Schedule not found: " + id));
        return scheduleMapper.toDto(schedule);
    }

    @Transactional
    public ScheduleDTO createSchedule(@Valid ScheduleDTO scheduleDTO) {
        validateSchedule(scheduleDTO);
        checkScheduleConflicts(scheduleDTO);
        Schedule schedule = scheduleMapper.toEntity(scheduleDTO);
        schedule.setId(null);
        setRelatedEntities(schedule, scheduleDTO);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(savedSchedule);
    }

    @Transactional
    public ScheduleDTO updateSchedule(Integer id, @Valid ScheduleDTO scheduleDTO) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found: " + id));
        validateSchedule(scheduleDTO);
        checkScheduleConflicts(scheduleDTO, id);
        scheduleMapper.partialUpdate(scheduleDTO, schedule);
        setRelatedEntities(schedule, scheduleDTO);
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(updatedSchedule);
    }

    @Transactional
    public void deleteSchedule(Integer id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found: " + id));
        schedule.softDelete();
        scheduleRepository.save(schedule);
    }

    private void validateSchedule(ScheduleDTO scheduleDTO) {
        if (scheduleDTO.getStartTime().isAfter(scheduleDTO.getEndTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be before end time");
        }
        if (!Set.of("ONLINE", "OFFLINE", "HYBRID").contains(scheduleDTO.getDeliveryMode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid delivery mode");
        }
        if ("ONLINE".equals(scheduleDTO.getDeliveryMode()) && (scheduleDTO.getMeetingLink() == null || scheduleDTO.getMeetingLink().trim().isEmpty())) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "Meeting link required for online delivery");
        }
        if (scheduleDTO.getGroupId() == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "Group ID is required");
        }
        if (scheduleDTO.getRoom() == null || scheduleDTO.getRoom().getId() == null) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "Room is required");
        }
    }

    private void checkScheduleConflicts(ScheduleDTO scheduleDTO, Integer... excludeId) {
        Integer excludeIdParam = excludeId.length > 0 ? excludeId[0] : null;

        // Uncomment these lines if existsRoomConflict and existsTeacherConflict are available in repository

        if (scheduleRepository.existsRoomConflict(
                scheduleDTO.getRoom().getId(),
                scheduleDTO.getStartTime(),
                scheduleDTO.getEndTime(),
                excludeIdParam)) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.CONFLICT, "Room is already booked for the specified time");
        }

        if (scheduleDTO.getTeacher() != null && scheduleDTO.getTeacher().getId() != null) {
            if (scheduleRepository.existsTeacherConflict(
                    scheduleDTO.getTeacher().getId(),
                    scheduleDTO.getStartTime(),
                    scheduleDTO.getEndTime(),
                    excludeIdParam)) {
                throw new org.springframework.web.server.ResponseStatusException(HttpStatus.CONFLICT, "Teacher is already assigned for the specified time");
            }
        }

    }

    private void setRelatedEntities(Schedule schedule, ScheduleDTO scheduleDTO) {
        Group group = groupRepository.findActiveById(scheduleDTO.getGroupId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + scheduleDTO.getGroupId()));
        schedule.setGroup(group);

        Room room = roomRepository.findActiveById(scheduleDTO.getRoom().getId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found: " + scheduleDTO.getRoom().getId()));
        schedule.setRoom(room);

        if (scheduleDTO.getTeacher() != null && scheduleDTO.getTeacher().getId() != null) {
            Teacher teacher = teacherRepository.findActiveById(scheduleDTO.getTeacher().getId())
                    .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found: " + scheduleDTO.getTeacher().getId()));
            schedule.setTeacher(teacher);
        } else {
            schedule.setTeacher(null); // Teacher is optional
        }
    }

    public Set<Schedule> generateWeeklySchedule(Integer groupId, OffsetDateTime startDate, OffsetDateTime endDate) {
        //validate input parameters
        Group group = groupRepository.findActiveById(groupId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + groupId)
        );

        if (startDate.isAfter(endDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be before end date");
        }

        if (startDate.isBefore(OffsetDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date must be in the future");
        }

        //check if group has schedules have fully between startDate and endDate skip
        List<Schedule> existingSchedules = scheduleRepository.findAllActivePageable(
                groupId, startDate, endDate, Pageable.unpaged()).getContent();
        if (!existingSchedules.isEmpty()) {
            existingSchedules.forEach(schedule -> {
                if (schedule.getEndTime().getDayOfMonth() == endDate.getDayOfMonth() &&
                        schedule.getEndTime().getMonthValue() == endDate.getMonthValue() &&
                        schedule.getEndTime().getYear() == endDate.getYear()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Group already has schedules between " + startDate + " and " + endDate);
                }
            }
        }


        Set<Schedule> schedules;

        if (group.getGroupSchedules().isEmpty()) {
            schedules = new LinkedHashSet<>();
        } else {
            schedules = group.getSchedules();
        }



        //

        // Generate weekly schedule logic
        // to generate weekly schedule for the group from startDate to endDate
        // Assuming we want to generate schedules for every week starting from startDate
        List<GroupSchedule> sortedMainSchedule = group.getGroupSchedules().stream()
                .sorted(Comparator.comparing(GroupSchedule::getDayOfWeekEnum))
                .toList();
        AtomicReference<OffsetDateTime> currentDate = new AtomicReference<>(startDate);
        sortedMainSchedule.forEach(groupSchedule -> {
            do {
                if (currentDate.get().getDayOfWeek().equals(groupSchedule.getDayOfWeekEnum())) {
                    // Create a new schedule for the group
                    Schedule schedule = Schedule.builder()
                            .group(group)
                            //get the date of the current date and set the start and end time from groupSchedule
                            .startTime(currentDate.get().withHour(groupSchedule.getStartTime().getHour())
                                    .withMinute(groupSchedule.getStartTime().getMinute())
                                    .withSecond(groupSchedule.getStartTime().getSecond()))
                            .endTime(currentDate.get().withHour(groupSchedule.getEndTime().getHour())
                                    .withMinute(groupSchedule.getEndTime().getMinute())
                                    .withSecond(groupSchedule.getEndTime().getSecond()))
                            .deliveryMode("OFFLINE") // Default delivery mode, can be changed as needed
                            .attendances(null)
                            .teacher(null) // Assuming no teacher is assigned, can be set later
                            .room(groupSchedule.getRoom()) // Assuming room is set in GroupSchedule
                            .meetingLink(null)
                            .build();
                    // Check for conflicts before saving
                    checkScheduleConflicts(scheduleMapper.toDto(schedule));
                    schedules.add(schedule);
                    break; // Exit the loop once a schedule is created for the current date
                }
                //move currentDate to next date
                currentDate.set(currentDate.get().plusDays(1));

            } while (currentDate.get().isBefore(endDate));
            // Reset currentDate to startDate for the next iteration
            currentDate.set(startDate);
        });
        // Save all schedules in one transaction
        if (!schedules.isEmpty()) {
            schedules.forEach(schedule -> {
                schedule.setId(null); // Ensure new schedules are created
                setRelatedEntities(schedule, scheduleMapper.toDto(schedule));
            });
            scheduleRepository.saveAll(schedules);
        }
        return schedules;
    }
}