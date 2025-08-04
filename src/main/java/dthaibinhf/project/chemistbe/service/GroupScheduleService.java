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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class GroupScheduleService {

    GroupScheduleRepository groupScheduleRepository;
    GroupScheduleMapper groupScheduleMapper;
    ScheduleRepository scheduleRepository;

    /**
     * Combines a LocalDate with a LocalTime to create an OffsetDateTime using Ho
     * Chi Minh City timezone.
     * This is used to convert GroupSchedule template times (LocalTime) with
     * schedule dates (LocalDate)
     * into Schedule entity times (OffsetDateTime).
     */
    private OffsetDateTime combineDateTime(LocalDate date, LocalTime time) {
        ZoneId hoChiMinhZone = ZoneId.of("Asia/Ho_Chi_Minh");
        return OffsetDateTime.of(date, time, hoChiMinhZone.getRules().getOffset(date.atTime(time)));
    }

    public List<GroupScheduleDTO> getAllGroupSchedules() {
        List<GroupSchedule> groupSchedules = groupScheduleRepository.findAllActiveGroupSchedule();
        return groupSchedules.stream().map(groupScheduleMapper::toDto).collect(Collectors.toList());
    }

    public GroupScheduleDTO getGroupScheduleById(Integer id) {
        GroupSchedule groupSchedule = groupScheduleRepository.findActiveById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group schedule not found with id: " + id));
        return groupScheduleMapper.toDto(groupSchedule);
    }

    public List<GroupScheduleDTO> getGroupScheduleByGroupId(Integer groupId) {
        return groupScheduleRepository.findAllActiveByGroupId(groupId).stream().map(groupScheduleMapper::toDto)
                .collect(Collectors.toList());
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group schedule not found with id: " + id));

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
        log.info("Starting cascade for GroupSchedule ID: {} in Group: {} - {} → {}", 
                groupSchedule.getId(), groupId, originalDayOfWeek, groupSchedule.getDayOfWeekEnum());

        // Get the current date /time
        OffsetDateTime now = OffsetDateTime.now();
        log.info("Current time for future schedule filtering: {}", now);

        // Find all active future schedules for this group using the dedicated method
        List<Schedule> futureSchedules = scheduleRepository.findActiveSchedulesByGroupIdAfterDate(groupId, now);
        log.info("Found {} future schedules for Group {}", futureSchedules.size(), groupId);
        
        // Log all future schedules for debugging
        futureSchedules.forEach(schedule -> 
            log.debug("Future schedule ID: {} - {} {} to {}", 
                    schedule.getId(), 
                    schedule.getStartTime().getDayOfWeek(),
                    schedule.getStartTime().toLocalDate(),
                    schedule.getStartTime()));

        // Filter schedules that match the original day of the week
        List<Schedule> matchingSchedules = futureSchedules.stream()
                .filter(schedule -> schedule.getStartTime().getDayOfWeek().equals(originalDayOfWeek))
                .collect(Collectors.toList());
        
        log.info("Found {} schedules matching original day {} for cascade", 
                matchingSchedules.size(), originalDayOfWeek);
        
        // Log matching schedules
        matchingSchedules.forEach(schedule -> 
            log.info("Matching schedule ID: {} - {} {} {}-{}", 
                    schedule.getId(),
                    schedule.getStartTime().getDayOfWeek(),
                    schedule.getStartTime().toLocalDate(),
                    schedule.getStartTime().toLocalTime(),
                    schedule.getEndTime().toLocalTime()));

        // Check if day of week changed
        DayOfWeek newDayOfWeek = groupSchedule.getDayOfWeekEnum();
        boolean dayChanged = !originalDayOfWeek.equals(newDayOfWeek);
        log.info("Day changed: {} ({} → {})", dayChanged, originalDayOfWeek, newDayOfWeek);

        // Update each matching schedule
        int updatedCount = 0;
        for (Schedule schedule : matchingSchedules) {
            LocalDate scheduleDate = schedule.getStartTime().toLocalDate();
            log.info("Processing schedule ID: {} on {}", schedule.getId(), scheduleDate);
            
            if (dayChanged) {
                // Calculate new date for the new day of week
                LocalDate newDate = calculateNewDateForDayChange(scheduleDate, originalDayOfWeek, newDayOfWeek);
                log.info("Day change: {} {} → {} {}", 
                        originalDayOfWeek, scheduleDate, newDayOfWeek, newDate);
                
                if (newDate != null) {
                    // Update to new day with new times
                    OffsetDateTime newStartTime = combineDateTime(newDate, groupSchedule.getStartTime());
                    OffsetDateTime newEndTime = combineDateTime(newDate, groupSchedule.getEndTime());
                    log.info("Schedule ID: {} updated - {} to {} | {}-{} to {}-{}",
                            schedule.getId(),
                            schedule.getStartTime().toLocalDate(), newDate,
                            schedule.getStartTime().toLocalTime(), schedule.getEndTime().toLocalTime(),
                            groupSchedule.getStartTime(), groupSchedule.getEndTime());
                    
                    schedule.setStartTime(newStartTime);
                    schedule.setEndTime(newEndTime);
                    updatedCount++;
                } else {
                    log.warn("Could not calculate new date for schedule ID: {}, skipping", schedule.getId());
                    continue;
                }
            } else {
                // Same day, just update times
                OffsetDateTime newStartTime = combineDateTime(scheduleDate, groupSchedule.getStartTime());
                OffsetDateTime newEndTime = combineDateTime(scheduleDate, groupSchedule.getEndTime());
                
                log.info("Schedule ID: {} time updated - {}-{} → {}-{}", 
                        schedule.getId(),
                        schedule.getStartTime().toLocalTime(), schedule.getEndTime().toLocalTime(),
                        groupSchedule.getStartTime(), groupSchedule.getEndTime());
                
                schedule.setStartTime(newStartTime);
                schedule.setEndTime(newEndTime);
                updatedCount++;
            }

            // Update room if changed
            if (groupSchedule.getRoom() != null) {
                String oldRoomName = schedule.getRoom() != null ? schedule.getRoom().getName() : "null";
                schedule.setRoom(groupSchedule.getRoom());
                log.info("Schedule ID: {} room updated - {} → {}", 
                        schedule.getId(), oldRoomName, groupSchedule.getRoom().getName());
            }

            // Note: GroupSchedule doesn't have teacher field, teacher updates handled separately
        }

        // Save all updated schedules
        if (!matchingSchedules.isEmpty()) {
            log.info("Saving {} updated schedules to database", matchingSchedules.size());
            try {
                scheduleRepository.saveAll(matchingSchedules);
                log.info("Successfully saved {} schedules. Cascade complete for GroupSchedule ID: {}", 
                        updatedCount, groupSchedule.getId());
            } catch (Exception e) {
                log.error("Error saving updated schedules for GroupSchedule ID: {}", groupSchedule.getId(), e);
                throw e;
            }
        } else {
            log.warn("No matching schedules found to update for GroupSchedule ID: {}", groupSchedule.getId());
        }
    }

    /**
     * Calculate new date when day of week changes.
     * For example: Monday July 29 → Tuesday should become Tuesday July 30
     */
    private LocalDate calculateNewDateForDayChange(LocalDate originalDate, DayOfWeek originalDay, DayOfWeek newDay) {
        // Calculate the difference in days
        int dayDifference = newDay.getValue() - originalDay.getValue();
        LocalDate currentDate = LocalDate.now();
        // Handle week wrap-around (e.g., Friday → Monday = +3 days, not -4)
        if (dayDifference < 0) {
            LocalDate newDate = originalDate.plusDays(dayDifference);
            if (newDate.isAfter(currentDate))
                return newDate;
            else dayDifference += 7;
        }
        
        return originalDate.plusDays(dayDifference);
    }

    @Transactional
    public void deleteGroupSchedule(Integer id) {
        GroupSchedule groupSchedule = groupScheduleRepository.findActiveById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group schedule not found with id: " + id));
        groupSchedule.softDelete();
        groupScheduleRepository.save(groupSchedule);
    }

}
