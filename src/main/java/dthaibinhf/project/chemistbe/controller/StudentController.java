package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.StudentDTO;
import dthaibinhf.project.chemistbe.dto.StudentDetailDTO;
import dthaibinhf.project.chemistbe.service.StudentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
     * Get a list of students by group ID
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

    @GetMapping("/search")
    public ResponseEntity<Page<StudentDTO>> searchStudents(
            @RequestParam(name = "studentName", required = false) String studentName,
            @RequestParam(name = "groupName", required = false) String groupName,
            @RequestParam(name = "schoolName",required = false) String schoolName,
            @RequestParam( name = "className",required = false) String className,
            @RequestParam(name = "parentPhone", required = false) String parentPhone,
            // Pagination parameters with default values
            @RequestParam(defaultValue = "0", name = "page") int page,           // Page number (0-based)
            @RequestParam(name = "size", defaultValue = "20") int size,         // Items per page
            @RequestParam(name = "sort", defaultValue = "id,asc") String sort   // Sort field and direction
    ) {
        // Parse multiple sort parameters
        String[] sortParams = sort.split(",");
        List<Sort.Order> orders = new ArrayList<>();

        for (int i = 0; i < sortParams.length; i += 2) {
            String field = sortParams[i];
            Sort.Direction direction = (i + 1 < sortParams.length && "desc".equalsIgnoreCase(sortParams[i + 1]))
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            orders.add(new Sort.Order(direction, field));
        }

        // Create a Pageable object
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        // Call service method
        Page<StudentDTO> resultPaging = studentService.search(pageable,
                studentName, groupName, schoolName, className, parentPhone);

        return ResponseEntity.ok(resultPaging);
    }
}
