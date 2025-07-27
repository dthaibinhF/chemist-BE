package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.StudentDTO;
import dthaibinhf.project.chemistbe.mapper.StudentDetailMapper;
import dthaibinhf.project.chemistbe.mapper.StudentMapper;
import dthaibinhf.project.chemistbe.model.Student;
import dthaibinhf.project.chemistbe.model.StudentDetail;
import dthaibinhf.project.chemistbe.dto.StudentDetailDTO;
import dthaibinhf.project.chemistbe.repository.GroupRepository;
import dthaibinhf.project.chemistbe.repository.StudentDetailRepository;
import dthaibinhf.project.chemistbe.repository.StudentRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentService {
    StudentRepository studentRepository;
    StudentDetailRepository studentDetailRepository;
    StudentMapper studentMapper;
    StudentDetailMapper studentDetailMapper;
    GroupRepository groupRepository;

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAllActive().stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
    }

    public StudentDTO getStudentById(Integer id) {
        Student student = studentRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + id));
        return studentMapper.toDto(student);
    }

    @Transactional
    public StudentDTO createStudent(StudentDTO studentDTO) {
        Student student = studentMapper.toEntity(studentDTO);
        student.setId(null);
        Student saved = studentRepository.save(student);
        return studentMapper.toDto(saved);
    }

    @Transactional
    public StudentDTO updateStudent(Integer id, StudentDTO studentDTO) {
        // find current active student by ID
        Student student = studentRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + id));
        // handle new changes of studentDTO

        // Update basic student information
        studentMapper.partialUpdate(studentDTO, student);

        // Handle student details - create new records instead of updating existing ones
        if (studentDTO.getStudentDetails() != null && !studentDTO.getStudentDetails().isEmpty()) {
            // Get current active student details
            Set<StudentDetail> currentDetails = student.getStudentDetails().stream()
                    .filter(detail -> detail.getEndAt() == null)
                    .collect(Collectors.toSet());

            // Soft delete current details
            currentDetails.forEach(StudentDetail::softDelete);

            // Create new student details
            Set<StudentDetail> newDetails = studentDTO.getStudentDetails().stream()
                    .map(studentDetailMapper::toEntity)
                    .peek(detail -> {
                        detail.setId(null); // Ensure new record is created
                        detail.setStudent(student);
                        if (detail.getGroup() != null && detail.getGroup().getId() != null) {
                            detail.setGroup(groupRepository.getReferenceById(detail.getGroup().getId()));
                        }
                    })
                    .collect(Collectors.toSet());

            // Add new details to student
            student.getStudentDetails().addAll(newDetails);
        }

        Student updated = studentRepository.save(student);
        return studentMapper.toDto(updated);
    }

    @Transactional
    public void deleteStudent(Integer id) {
        Student student = studentRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + id));
        student.softDelete();
        studentRepository.save(student);
    }

    @Transactional
    public List<StudentDTO> createMultipleStudent(List<StudentDTO> studentDTOList) {
        if (studentDTOList != null && !studentDTOList.isEmpty()) {
            List<Student> students = studentDTOList.stream()
                    .map(studentMapper::toEntity)
                    .peek(student -> {
                        if (student.getStudentDetails() != null) {
                            student.getStudentDetails().forEach(detail -> {
                                detail.setStudent(student);
                                if (detail.getGroup() != null && detail.getGroup().getId() != null) {
                                    detail.setGroup(groupRepository.getReferenceById(detail.getGroup().getId()));
                                }
                            });
                        }
                    })
                    .collect(Collectors.toList());
            List<Student> savedStudents = studentRepository.saveAll(students);
            return savedStudents.stream().map(studentMapper::toDto).collect(Collectors.toList());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student list cannot be null or empty");
        }
    }

    /**
     * Get list of students by group ID
     * Returns only the newest non-deleted student detail for each student in the
     * group
     *
     * @param groupId the ID of the group
     * @return list of students with their newest non-deleted student detail for the
     *         specified group
     */
    public List<StudentDTO> getStudentsByGroupId(Integer groupId) {
        if (groupId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group ID cannot be null");
        }

        // Find students with their newest non-deleted student details for the specified
        // group
        List<Student> students = studentRepository.findByGroupIdWithNewestActiveDetails(groupId);

        // Convert students to DTOs
        return students.stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get history of student details for a specific student
     *
     * @param studentId the ID of the student
     * @return list of all student details for the student, ordered by creation date
     *         (newest first)
     */
    public List<StudentDetailDTO> getStudentDetailHistory(Integer studentId) {
        if (studentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student ID cannot be null");
        }

        // Check if student exists
        studentRepository.findActiveById(studentId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + studentId));

        // Get all student details for the student, ordered by creation date
        List<StudentDetail> studentDetails = studentDetailRepository.findAllByStudentIdOrderByCreatedAtDesc(studentId);

        // Convert to DTOs
        return studentDetails.stream()
                .map(studentDetailMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Search students with pagination and sorting
     * Search by student ID, phone, name, group name, school name, or class name
     *
     * @param studentId   search by student ID (exact match)
     * @param parentPhone search by parent phone (contains)
     * @param studentName search by student name (contains, case-insensitive)
     * @param groupName   search by group name (contains, case-insensitive)
     * @param schoolName  search by school name (contains, case-insensitive)
     * @param className   search by school class name (contains, case-insensitive)
     * @param pageable    pagination and sorting parameters
     * @return page of students matching the criteria
     */
    public Page<StudentDTO> search(Pageable pageable,
            String studentName,
            String groupName,
            String schoolName,
            String className,
            String parentPhone) {
        try {
            log.info("Searching students - page: {}, size: {}, sort: {}",
                    pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
            // Prepare search patterns for LIKE queries
            // Add wildcards for LIKE queries
            String studentNamePattern = !studentName.isEmpty() ? "%" + studentName + "%" : null;
            String groupNamePattern = !groupName.isEmpty() ? "%" + groupName + "%" : null;
            String schoolNamePattern = !schoolName.isEmpty() ? "%" + schoolName + "%" : null;
            String classNamePattern = !className.isEmpty() ? "%" + className + "%" : null;
            String parentPhonePattern = !parentPhone.isEmpty() ? "%" + parentPhone + "%" : null;

            // Call repository method
            Page<Student> studentsPage = studentRepository.testSearchStudents(
                    studentNamePattern,
                    groupNamePattern,
                    schoolNamePattern,
                    classNamePattern,
                    parentPhonePattern,
                    pageable);

            log.info("Search result: {}", studentsPage.getContent());

            // Convert Page<Entity> to Page<DTO> using map()
            return studentsPage.map(studentMapper::toDto);

        } catch (Exception e) {
            log.error("Error searching students with pagination", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to search students");
        }
    }
}