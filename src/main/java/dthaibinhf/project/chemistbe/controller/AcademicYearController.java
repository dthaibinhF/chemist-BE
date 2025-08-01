package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.AcademicYearDTO;
import dthaibinhf.project.chemistbe.service.AcademicYearService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/academic-year")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class AcademicYearController {

    AcademicYearService academicYearService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<AcademicYearDTO> createAcademicYear(@Valid @RequestBody AcademicYearDTO academicYearDTO) {
        return ResponseEntity.ok(academicYearService.createAcademicYear(academicYearDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<AcademicYearDTO> getAcademicYear(@PathVariable Integer id) {
        return ResponseEntity.ok(academicYearService.getAcademicYearById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<List<AcademicYearDTO>> getAllAcademicYears() {
        return ResponseEntity.ok(academicYearService.getAllAcademicYears());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<AcademicYearDTO> updateAcademicYear(@PathVariable Integer id, @Valid @RequestBody AcademicYearDTO academicYearDTO) {
        return ResponseEntity.ok(academicYearService.updateAcademicYear(id, academicYearDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteAcademicYear(@PathVariable Integer id) {
        academicYearService.deleteAcademicYear(id);
        return ResponseEntity.noContent().build();
    }
}