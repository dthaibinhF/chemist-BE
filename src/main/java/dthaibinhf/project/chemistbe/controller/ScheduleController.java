package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.ScheduleDTO;
import dthaibinhf.project.chemistbe.service.ScheduleService;
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
}
