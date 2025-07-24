package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.AttendanceDTO;
import dthaibinhf.project.chemistbe.dto.BulkAttendanceDTO;
import dthaibinhf.project.chemistbe.service.AttendanceService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceController {
    AttendanceService attendanceService;

    @GetMapping
    public ResponseEntity<List<AttendanceDTO>> getAllAttendances() {
        return ResponseEntity.ok(attendanceService.getAllAttendances());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDTO> getAttendanceById(@PathVariable Integer id) {
        return ResponseEntity.ok(attendanceService.getAttendanceById(id));
    }

    @PostMapping
    public ResponseEntity<AttendanceDTO> createAttendance(@RequestBody AttendanceDTO attendanceDTO) {
        return ResponseEntity.ok(attendanceService.createAttendance(attendanceDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDTO> updateAttendance(@PathVariable Integer id, @RequestBody AttendanceDTO attendanceDTO) {
        return ResponseEntity.ok(attendanceService.updateAttendance(id, attendanceDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Integer id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<AttendanceDTO>> searchAttendanceByGroupAndSchedule(
            @RequestParam(value = "groupId", required = false) Integer groupId,
            @RequestParam(value = "scheduleId", required = false) Integer scheduleId
    ) {
        return ResponseEntity.ok(attendanceService.searchAttendanceByGroupAndSchedule(groupId, scheduleId));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<AttendanceDTO>> createBulkAttendance(@RequestBody BulkAttendanceDTO bulkAttendanceDTO) {
        return ResponseEntity.ok(attendanceService.createBulkAttendance(bulkAttendanceDTO));
    }

    @PutMapping("/bulk")
    public ResponseEntity<List<AttendanceDTO>> updateBulkAttendance(@RequestBody BulkAttendanceDTO bulkAttendanceDTO) {
        return ResponseEntity.ok(attendanceService.updateBulkAttendance(bulkAttendanceDTO));
    }
}
