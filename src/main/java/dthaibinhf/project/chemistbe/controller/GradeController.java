package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.GradeDTO;
import dthaibinhf.project.chemistbe.service.GradeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grade")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class GradeController {

    GradeService gradeService;

    @PostMapping
    public ResponseEntity<GradeDTO> createGrade(@Valid @RequestBody GradeDTO gradeDTO) {
        return ResponseEntity.ok(gradeService.createGrade(gradeDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GradeDTO> getGrade(@PathVariable Integer id) {
        return ResponseEntity.ok(gradeService.getGradeById(id));
    }

    @GetMapping
    public ResponseEntity<List<GradeDTO>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeDTO> updateGrade(@PathVariable Integer id, @Valid @RequestBody GradeDTO gradeDTO) {
        return ResponseEntity.ok(gradeService.updateGrade(id, gradeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Integer id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
}