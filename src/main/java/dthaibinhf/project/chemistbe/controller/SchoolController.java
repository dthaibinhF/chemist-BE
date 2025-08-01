package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.SchoolDTO;
import dthaibinhf.project.chemistbe.service.SchoolService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/school")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class SchoolController {

    SchoolService schoolService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SchoolDTO> createSchool(@Valid @RequestBody SchoolDTO schoolDTO) {
        return ResponseEntity.ok(schoolService.createSchool(schoolDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<SchoolDTO> getSchool(@PathVariable Integer id) {
        return ResponseEntity.ok(schoolService.getSchoolById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<List<SchoolDTO>> getAllSchools() {
        return ResponseEntity.ok(schoolService.getAllSchools());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SchoolDTO> updateSchool(@PathVariable Integer id, @Valid @RequestBody SchoolDTO schoolDTO) {
        return ResponseEntity.ok(schoolService.updateSchool(id, schoolDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteSchool(@PathVariable Integer id) {
        schoolService.deleteSchool(id);
        return ResponseEntity.noContent().build();
    }

}
