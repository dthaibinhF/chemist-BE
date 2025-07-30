package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.GroupSessionDTO;
import dthaibinhf.project.chemistbe.service.GroupSessionService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/group-sessions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class GroupSessionController {

    GroupSessionService groupSessionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<List<GroupSessionDTO>> getAllGroupSessions() {
        return ResponseEntity.ok(groupSessionService.getAllGroupSessions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<GroupSessionDTO> getGroupSessionById(@PathVariable Integer id) {
        return ResponseEntity.ok(groupSessionService.getGroupSessionById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<GroupSessionDTO> createGroupSession(@RequestBody GroupSessionDTO groupSessionDTO) {
        return ResponseEntity.ok(groupSessionService.createGroupSession(groupSessionDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<GroupSessionDTO> updateGroupSession(@PathVariable Integer id, @RequestBody GroupSessionDTO groupSessionDTO) {
        return ResponseEntity.ok(groupSessionService.updateGroupSession(id, groupSessionDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteGroupSession(@PathVariable Integer id) {
        groupSessionService.deleteGroupSession(id);
        return ResponseEntity.noContent().build();
    }
}