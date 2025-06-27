package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.StudentDTO;
import dthaibinhf.project.chemistbe.mapper.StudentMapper;
import dthaibinhf.project.chemistbe.model.Student;
import dthaibinhf.project.chemistbe.repository.StudentRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StudentService {
    StudentRepository studentRepository;
    StudentMapper studentMapper;

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
        Student student = studentRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + id));
        studentMapper.partialUpdate(studentDTO, student);
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
} 