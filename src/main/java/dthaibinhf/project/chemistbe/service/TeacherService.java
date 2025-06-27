package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.TeacherDTO;
import dthaibinhf.project.chemistbe.mapper.TeacherMapper;
import dthaibinhf.project.chemistbe.model.Teacher;
import dthaibinhf.project.chemistbe.repository.AccountRepository;
import dthaibinhf.project.chemistbe.repository.TeacherRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class TeacherService {

    TeacherRepository teacherRepository;
    AccountRepository accountRepository;
    TeacherMapper teacherMapper;

    @Cacheable(value = "teachers", key = "'allTeachers'")
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAllActiveTeachers().stream()
                .map(teacherMapper::toDto)
                .collect(Collectors.toList());
    }

    public TeacherDTO getTeacherById(Integer id) {
        Teacher teacher = teacherRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found: " + id));
        return teacherMapper.toDto(teacher);
    }

    @Transactional
    @CacheEvict(value = "teachers", allEntries = true)
    public TeacherDTO createTeacher(@Valid TeacherDTO teacherDTO) {
        Teacher teacher = teacherMapper.toEntity(teacherDTO);
        teacher.setId(null);
        Teacher savedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toDto(savedTeacher);
    }

    @Transactional
    @CacheEvict(value = "teachers", allEntries = true)
    public TeacherDTO updateTeacher(Integer id, @Valid TeacherDTO teacherDTO) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found: " + id));
        teacherMapper.partialUpdate(teacherDTO, teacher);
        Teacher updatedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toDto(updatedTeacher);
    }

    @Transactional
    @CacheEvict(value = "teachers", allEntries = true)
    public void deleteTeacher(Integer id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found: " + id));
        teacher.softDelete();
        teacherRepository.save(teacher);
    }
}