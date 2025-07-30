package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.*;
import dthaibinhf.project.chemistbe.service.ScheduleService;
import dthaibinhf.project.chemistbe.service.ScheduledScheduleService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/schedule")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ScheduleController {

    ScheduleService scheduleService;
    ScheduledScheduleService scheduledScheduleService;

    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ScheduleDTO>> getAllSchedulesPageable(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "groupId", required = false) Integer groupId,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate) {
        return ResponseEntity.ok(scheduleService.getAllSchedulesPageable(pageable, groupId, startDate, endDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleDTO> getScheduleById(@PathVariable Integer id) {
        return ResponseEntity.ok(scheduleService.getScheduleById(id));
    }

    @PostMapping
    public ResponseEntity<ScheduleDTO> createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        return ResponseEntity.ok(scheduleService.createSchedule(scheduleDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScheduleDTO> updateSchedule(@PathVariable Integer id, @RequestBody ScheduleDTO scheduleDTO) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, scheduleDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Integer id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    /* Schedule Generation Feature Implementation */
    /* creates schedules for one group, From start date to end date */
    @PostMapping("/weekly")
    public ResponseEntity<Set<ScheduleDTO>> generateWeeklySchedule(
            @RequestParam Integer groupId,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate startDate, // date when the
                                                                                                 // schedule starts
            @RequestParam LocalDate endDate // date when the schedule ends
    ) {
        return ResponseEntity.ok(scheduleService.generateWeeklySchedule(groupId, startDate, endDate));
    }

    /* Bulk Schedule Generation Endpoints */
    
    /**
     * Generate schedules for selected groups
     */
    @PostMapping("/bulk/selected-groups")
    public ResponseEntity<List<Set<ScheduleDTO>>> generateBulkSchedules(
            @RequestBody BulkScheduleRequest request) {
        List<Set<ScheduleDTO>> results = scheduleService.generateBulkWeeklySchedules(
                request.getGroupIds(), 
                request.getStartDate(), 
                request.getEndDate()
        );
        return ResponseEntity.ok(results);
    }

    /**
     * Generate schedules for all active groups
     */
    @PostMapping("/bulk/all-groups")
    public ResponseEntity<List<Set<ScheduleDTO>>> generateSchedulesForAllGroups(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<Set<ScheduleDTO>> results = scheduleService.generateSchedulesForAllActiveGroups(startDate, endDate);
        return ResponseEntity.ok(results);
    }

    /**
     * Generate schedules for next week for all groups (same as automatic Monday job)
     */
    @PostMapping("/bulk/next-week")
    public ResponseEntity<String> generateNextWeekSchedules() {
        LocalDate nextMonday = LocalDate.now().with(java.time.DayOfWeek.MONDAY).plusDays(7);
        LocalDate nextSunday = nextMonday.plusDays(6);
        scheduleService.generateSchedulesForAllActiveGroups(nextMonday, nextSunday);
        return ResponseEntity.ok("Next week schedule generation triggered successfully");
    }

    /**
     * Manual trigger for automatic generation (same logic as Monday job)
     */
    @PostMapping("/auto-generation/trigger")
    public ResponseEntity<String> triggerAutoGeneration() {
        scheduledScheduleService.triggerWeeklyGeneration();
        return ResponseEntity.ok("Automatic schedule generation triggered successfully");
    }

    /* Future Schedule Update Endpoints */
    
    // TODO: Temporarily removed updateScheduleWithMode - missing ScheduleUpdateRequest/Response DTOs

    /**
     * Get count of future schedules that would be affected by update
     */
    @GetMapping("/{id}/future-count")
    public ResponseEntity<Integer> getFutureSchedulesCount(@PathVariable Integer id) {
        int count = scheduleService.getFutureSchedulesCount(id);
        return ResponseEntity.ok(count);
    }
}
