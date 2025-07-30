
package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.GroupDTO;
import dthaibinhf.project.chemistbe.dto.GroupListDTO;
import dthaibinhf.project.chemistbe.service.GroupService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/group")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class GroupController {

    GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupDTO> createGroup(@Valid @RequestBody GroupDTO groupDTO) {
        return ResponseEntity.ok(groupService.createGroup(groupDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupDTO> getGroup(@PathVariable Integer id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @GetMapping("/name/{groupName}")
    public ResponseEntity<GroupListDTO> getGroupByGroupName(@PathVariable String groupName) {
        return ResponseEntity.ok(groupService.getGroupByGroupName(groupName));
    }

    @GetMapping
    public ResponseEntity<List<GroupListDTO>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/detail")
    public ResponseEntity<List<GroupDTO>> getAllGroupWithDetail() {
        return ResponseEntity.ok(groupService.getAllGroupsWithDetail());
    }

    @GetMapping("/academic-year/{academicYearId}")
    public ResponseEntity<List<GroupListDTO>> getGroupsByAcademicYearId(@PathVariable Integer academicYearId) {
        return ResponseEntity.ok(groupService.getGroupsByAcademicYearId(academicYearId));
    }

    @GetMapping("/grade/{gradeId}")
    public ResponseEntity<List<GroupListDTO>> getGroupsByGradeId(@PathVariable Integer gradeId) {
        return ResponseEntity.ok(groupService.getGroupsByGradeId(gradeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupDTO> updateGroup(
            @PathVariable Integer id, 
            @Valid @RequestBody GroupDTO groupDTO,
            @RequestParam(defaultValue = "true") boolean syncFutureSchedules) {
        return ResponseEntity.ok(groupService.updateGroup(id, groupDTO, syncFutureSchedules));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Integer id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }
}
