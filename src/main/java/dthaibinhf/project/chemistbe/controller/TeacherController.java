package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.TeacherDTO;
import dthaibinhf.project.chemistbe.service.TeacherService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class TeacherController {

    TeacherService teacherService;

    @PostMapping
    public ResponseEntity<TeacherDTO> createTeacher(@Valid @RequestBody TeacherDTO teacherDTO) {
        return ResponseEntity.ok(teacherService.createTeacher(teacherDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherDTO> getTeacher(@PathVariable Integer id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @GetMapping
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherDTO> updateTeacher(@PathVariable Integer id, @Valid @RequestBody TeacherDTO teacherDTO) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacherDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Integer id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}