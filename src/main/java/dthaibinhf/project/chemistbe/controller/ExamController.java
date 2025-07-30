package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.ExamDTO;
import dthaibinhf.project.chemistbe.service.ExamService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exam")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ExamController {

    ExamService examService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<ExamDTO> createExam(@Valid @RequestBody ExamDTO examDTO) {
        return ResponseEntity.ok(examService.createExam(examDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<ExamDTO> getExam(@PathVariable Integer id) {
        return ResponseEntity.ok(examService.getExamById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<List<ExamDTO>> getAllExams() {
        return ResponseEntity.ok(examService.getAllExams());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<ExamDTO> updateExam(@PathVariable Integer id, @Valid @RequestBody ExamDTO examDTO) {
        return ResponseEntity.ok(examService.updateExam(id, examDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteExam(@PathVariable Integer id) {
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}
