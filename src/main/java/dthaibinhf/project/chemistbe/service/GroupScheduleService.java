package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.GroupScheduleDTO;
import dthaibinhf.project.chemistbe.mapper.GroupScheduleMapper;
import dthaibinhf.project.chemistbe.model.GroupSchedule;
import dthaibinhf.project.chemistbe.model.Schedule;
import dthaibinhf.project.chemistbe.repository.GroupScheduleRepository;
import dthaibinhf.project.chemistbe.repository.ScheduleRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupScheduleService {

    GroupScheduleRepository groupScheduleRepository;
    GroupScheduleMapper groupScheduleMapper;
    ScheduleRepository scheduleRepository;


    public List<GroupScheduleDTO> getAllGroupSchedules() {
        List<GroupSchedule> groupSchedules = groupScheduleRepository.findAllActiveGroupSchedule();
        return groupSchedules.stream().map(groupScheduleMapper::toDto).collect(Collectors.toList());
    }

    public GroupScheduleDTO getGroupScheduleById(Integer id) {
        GroupSchedule groupSchedule = groupScheduleRepository.findActiveById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group schedule not found with id: " + id)
        );
        return groupScheduleMapper.toDto(groupSchedule);
    }

    public List<GroupScheduleDTO> getGroupScheduleByGroupId(Integer groupId) {
        return groupScheduleRepository.findAllActiveByGroupId(groupId).stream().map(groupScheduleMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public GroupScheduleDTO createGroupSchedule(@Valid GroupScheduleDTO groupScheduleDTO) {
        GroupSchedule groupSchedule = groupScheduleMapper.toEntity(groupScheduleDTO);
        groupSchedule.setId(null);
        groupScheduleRepository.save(groupSchedule);
        return groupScheduleMapper.toDto(groupSchedule);
    }

    @Transactional
    public GroupScheduleDTO updateGroupSchedule(Integer id, @Valid GroupScheduleDTO groupScheduleDTO) {
        GroupSchedule groupSchedule = groupScheduleRepository.findActiveById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group schedule not found with id: " + id)
        );

        // Store the original day of week before updating
        DayOfWeek originalDayOfWeek = groupSchedule.getDayOfWeekEnum();

        // Update the group schedule
        groupScheduleMapper.partialUpdate(groupScheduleDTO, groupSchedule);

        // Get the updated group schedule
        GroupSchedule updatedGroupSchedule = groupScheduleRepository.save(groupSchedule);

        // Now update all related schedules
        updateRelatedSchedules(updatedGroupSchedule, originalDayOfWeek);

        return groupScheduleMapper.toDto(updatedGroupSchedule);
    }

    @Transactional
    public void updateRelatedSchedules(GroupSchedule groupSchedule, DayOfWeek originalDayOfWeek) {
        // Get the group ID
        Integer groupId = groupSchedule.getGroup().getId();

        // Get current date/time
        OffsetDateTime now = OffsetDateTime.now();

        // Find all active schedules for this group that are in the future
        List<Schedule> schedules = scheduleRepository.findAllActivePageable(
                groupId, now, null, Pageable.unpaged()).getContent();

        // Filter schedules that match the original day of week
        List<Schedule> matchingSchedules = schedules.stream()
                .filter(schedule -> schedule.getStartTime().getDayOfWeek().equals(originalDayOfWeek))
                .collect(Collectors.toList());

        // Update each matching schedule
        for (Schedule schedule : matchingSchedules) {
            // Update the start time while preserving the date
            OffsetDateTime newStartTime = schedule.getStartTime()
                    .withHour(groupSchedule.getStartTime().getHour())
                    .withMinute(groupSchedule.getStartTime().getMinute())
                    .withSecond(groupSchedule.getStartTime().getSecond());

            // Update the end time while preserving the date
            OffsetDateTime newEndTime = schedule.getEndTime()
                    .withHour(groupSchedule.getEndTime().getHour())
                    .withMinute(groupSchedule.getEndTime().getMinute())
                    .withSecond(groupSchedule.getEndTime().getSecond());

            // Set the new times
            schedule.setStartTime(newStartTime);
            schedule.setEndTime(newEndTime);

            // If the room has changed in the group schedule, update it in the schedule too
            if (groupSchedule.getRoom() != null) {
                schedule.setRoom(groupSchedule.getRoom());
            }
        }

        // Save all updated schedules
        if (!matchingSchedules.isEmpty()) {
            scheduleRepository.saveAll(matchingSchedules);
        }
    }

    @Transactional
    public void deleteGroupSchedule(Integer id) {
        GroupSchedule groupSchedule = groupScheduleRepository.findActiveById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group schedule not found with id: " + id)
        );
        groupSchedule.softDelete();
        groupScheduleRepository.save(groupSchedule);
    }

}
