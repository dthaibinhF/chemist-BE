package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.SchoolClassDTO;
import dthaibinhf.project.chemistbe.mapper.SchoolClassMapper;
import dthaibinhf.project.chemistbe.model.SchoolClass;
import dthaibinhf.project.chemistbe.repository.SchoolClassRepository;
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
public class SchoolClassService {

    SchoolClassRepository schoolClassRepository;
    SchoolClassMapper schoolClassMapper;

    @Cacheable("school-classes")
    public List<SchoolClassDTO> getAllSchoolClasses() {
        return schoolClassRepository.findAllActiveSchoolClasses().stream()
                .map(schoolClassMapper::toDto)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "school-classes", key = "#id")
    public SchoolClassDTO getSchoolClassById(Integer id) {
        SchoolClass schoolClass = schoolClassRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School Class not found: " + id));
        return schoolClassMapper.toDto(schoolClass);
    }

    @Transactional
    @CacheEvict(value = "school-classes", allEntries = true)
    public SchoolClassDTO createSchoolClass(@Valid SchoolClassDTO schoolClassDTO) {
        SchoolClass schoolClass = schoolClassMapper.toEntity(schoolClassDTO);
        schoolClass.setId(null);
        SchoolClass savedSchoolClass = schoolClassRepository.save(schoolClass);
        return schoolClassMapper.toDto(savedSchoolClass);
    }

    @Transactional
    @CacheEvict(value = "school-classes", allEntries = true)
    public SchoolClassDTO updateSchoolClass(Integer id, @Valid SchoolClassDTO schoolClassDTO) {
        SchoolClass schoolClass = schoolClassRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School Class not found: " + id));
        schoolClassMapper.partialUpdate(schoolClassDTO, schoolClass);
        SchoolClass updatedSchoolClass = schoolClassRepository.save(schoolClass);
        return schoolClassMapper.toDto(updatedSchoolClass);
    }

    @Transactional
    @CacheEvict(value = "school-classes", allEntries = true)
    public void deleteSchoolClass(Integer id) {
        SchoolClass schoolClass = schoolClassRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School Class not found: " + id));
        schoolClass.softDelete();
        schoolClassRepository.save(schoolClass);
    }

    @Cacheable(value = "school-classes", key = "'grade_' + #gradePrefix")
    public List<SchoolClassDTO> getSchoolClassesByGrade(Integer gradePrefix) {
        List<SchoolClass> schoolClasses = schoolClassRepository.findAllActiveByGrade(gradePrefix);
        return schoolClasses.stream().map(schoolClassMapper::toDto).collect(Collectors.toList());
    }
}