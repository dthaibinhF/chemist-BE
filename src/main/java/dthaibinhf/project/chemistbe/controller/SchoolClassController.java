package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.SchoolClassDTO;
import dthaibinhf.project.chemistbe.service.SchoolClassService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/school-class")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class SchoolClassController {

    SchoolClassService schoolClassService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SchoolClassDTO> createSchoolClass(@Valid @RequestBody SchoolClassDTO schoolClassDTO) {
        return ResponseEntity.ok(schoolClassService.createSchoolClass(schoolClassDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<SchoolClassDTO> getSchoolClass(@PathVariable Integer id) {
        return ResponseEntity.ok(schoolClassService.getSchoolClassById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<List<SchoolClassDTO>> getAllSchoolClasses() {
        return ResponseEntity.ok(schoolClassService.getAllSchoolClasses());
    }

    @GetMapping("/grade/{grade}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<List<SchoolClassDTO>> getSchoolClassesByGradeId(@PathVariable Integer grade) {
        return ResponseEntity.ok(schoolClassService.getSchoolClassesByGrade(grade));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SchoolClassDTO> updateSchoolClass(@PathVariable Integer id, @Valid @RequestBody SchoolClassDTO schoolClassDTO) {
        return ResponseEntity.ok(schoolClassService.updateSchoolClass(id, schoolClassDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteSchoolClass(@PathVariable Integer id) {
        schoolClassService.deleteSchoolClass(id);
        return ResponseEntity.noContent().build();
    }
}