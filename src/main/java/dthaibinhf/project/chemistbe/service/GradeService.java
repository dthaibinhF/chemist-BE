package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.GradeDTO;
import dthaibinhf.project.chemistbe.mapper.GradeMapper;
import dthaibinhf.project.chemistbe.model.Grade;
import dthaibinhf.project.chemistbe.repository.GradeRepository;
import jakarta.validation.Valid;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class GradeService {

    GradeRepository gradeRepository;
    GradeMapper gradeMapper;

    public List<GradeDTO> getAllGrades() {
        return gradeRepository.findAllActiveGrades().stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }

    public GradeDTO getGradeById(Integer id) {
        Grade grade = gradeRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found: " + id));
        return gradeMapper.toDto(grade);
    }

    @Transactional
    public GradeDTO createGrade(@Valid GradeDTO gradeDTO) {
        Grade grade = gradeMapper.toEntity(gradeDTO);
        grade.setId(null);
        Grade savedGrade = gradeRepository.save(grade);
        return gradeMapper.toDto(savedGrade);
    }

    @Transactional
    public GradeDTO updateGrade(Integer id, @Valid GradeDTO gradeDTO) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found: " + id));
        gradeMapper.partialUpdate(gradeDTO, grade);
        Grade updatedGrade = gradeRepository.save(grade);
        return gradeMapper.toDto(updatedGrade);
    }

    @Transactional
    public void deleteGrade(Integer id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found: " + id));
        grade.softDelete();
        gradeRepository.save(grade);
    }
}