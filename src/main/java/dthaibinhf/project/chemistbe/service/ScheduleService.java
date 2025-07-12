package dthaibinhf.project.chemistbe.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dthaibinhf.project.chemistbe.dto.ScheduleDTO;
import dthaibinhf.project.chemistbe.mapper.ScheduleMapper;
import dthaibinhf.project.chemistbe.model.Group;
import dthaibinhf.project.chemistbe.model.Room;
import dthaibinhf.project.chemistbe.model.Schedule;
import dthaibinhf.project.chemistbe.model.Teacher;
import dthaibinhf.project.chemistbe.repository.GroupRepository;
import dthaibinhf.project.chemistbe.repository.RoomRepository;
import dthaibinhf.project.chemistbe.repository.ScheduleRepository;
import dthaibinhf.project.chemistbe.repository.TeacherRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ScheduleService {

    ScheduleRepository scheduleRepository;
    ScheduleMapper scheduleMapper;
    GroupRepository groupRepository;
    TeacherRepository teacherRepository;
    RoomRepository roomRepository;

    public List<ScheduleDTO> getAllSchedules(Pageable pageable, Integer groupId, OffsetDateTime startDate, OffsetDateTime endDate) {
        List<Schedule> schedules = scheduleRepository.findAllActive();
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
        /*
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
        */
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
}