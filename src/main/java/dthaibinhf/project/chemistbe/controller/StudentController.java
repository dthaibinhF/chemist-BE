package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.StudentDTO;
import dthaibinhf.project.chemistbe.model.StudentDetailDTO;
import dthaibinhf.project.chemistbe.service.StudentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentController {
    StudentService studentService;

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Integer id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PostMapping
    public ResponseEntity<StudentDTO> createStudent(@RequestBody StudentDTO studentDTO) {
        return ResponseEntity.ok(studentService.createStudent(studentDTO));
    }

    @PostMapping("/multiple")
    public ResponseEntity<List<StudentDTO>> createMultipleStudent(@RequestBody List<StudentDTO> studentDTOList) {
        return ResponseEntity.ok(studentService.createMultipleStudent(studentDTOList));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> updateStudent(@PathVariable Integer id, @RequestBody StudentDTO studentDTO) {
        return ResponseEntity.ok(studentService.updateStudent(id, studentDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get list of students by group ID
     * Returns only the newest non-deleted student detail for each student in the group
     *
     * @param groupId the ID of the group
     * @return list of students with their newest non-deleted student detail for the specified group
     */
    @GetMapping("/by-group/{groupId}")
    public ResponseEntity<List<StudentDTO>> getStudentsByGroupId(@PathVariable Integer groupId) {
        return ResponseEntity.ok(studentService.getStudentsByGroupId(groupId));
    }

    /**
     * Get history of student details for a specific student
     *
     * @param studentId the ID of the student
     * @return list of all student details for the student, ordered by creation date (newest first)
     */
    @GetMapping("/{studentId}/detail-history")
    public ResponseEntity<List<StudentDetailDTO>> getStudentDetailHistory(@PathVariable Integer studentId) {
        return ResponseEntity.ok(studentService.getStudentDetailHistory(studentId));
    }
}
