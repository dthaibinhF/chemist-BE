package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.GroupScheduleDTO;
import dthaibinhf.project.chemistbe.service.GroupScheduleService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/group-schedule")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class GroupScheduleController {
    GroupScheduleService groupScheduleService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<List<GroupScheduleDTO>> getAllGroupSchedules() {
        return ResponseEntity.ok(groupScheduleService.getAllGroupSchedules());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<GroupScheduleDTO> getGroupScheduleById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(groupScheduleService.getGroupScheduleById(id));
    }

    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<List<GroupScheduleDTO>> getGroupScheduleByGroupId(@PathVariable("groupId") Integer groupId) {
        return ResponseEntity.ok(groupScheduleService.getGroupScheduleByGroupId(groupId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<GroupScheduleDTO> createGroupSchedule(@RequestBody GroupScheduleDTO groupSchedule) {
        return ResponseEntity.ok(groupScheduleService.createGroupSchedule(groupSchedule));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<GroupScheduleDTO> updateGroupSchedule(@PathVariable("id") Integer id, @RequestBody GroupScheduleDTO groupSchedule) {
        return ResponseEntity.ok(groupScheduleService.updateGroupSchedule(id, groupSchedule));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteGroupSchedule(@PathVariable("id") Integer id) {
        groupScheduleService.deleteGroupSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
