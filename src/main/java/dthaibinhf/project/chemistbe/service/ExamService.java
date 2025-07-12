package dthaibinhf.project.chemistbe.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import dthaibinhf.project.chemistbe.dto.ExamDTO;
import dthaibinhf.project.chemistbe.mapper.ExamMapper;
import dthaibinhf.project.chemistbe.model.Exam;
import dthaibinhf.project.chemistbe.repository.ExamRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class ExamService {

    ExamRepository examRepository;
    ExamMapper examMapper;

    public List<ExamDTO> getAllExams() {
        return examRepository.findAllActiveExams().stream()
                .map(examMapper::toDto)
                .collect(Collectors.toList());
    }

    public ExamDTO getExamById(Integer id) {
        Exam exam = examRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found: " + id));
        return examMapper.toDto(exam);
    }

    @Transactional
    public ExamDTO createExam(@Valid ExamDTO examDTO) {
        Exam exam = examMapper.toEntity(examDTO);
        exam.setId(null);
        Exam savedExam = examRepository.save(exam);
        return examMapper.toDto(savedExam);
    }

    @Transactional
    public ExamDTO updateExam(Integer id, @Valid ExamDTO examDTO) {
        Exam exam = examRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found: " + id));
        examMapper.partialUpdate(examDTO, exam);
        Exam updatedExam = examRepository.save(exam);
        return examMapper.toDto(updatedExam);
    }

    @Transactional
    public void deleteExam(Integer id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found: " + id));
        exam.softDelete();
        examRepository.save(exam);
    }
}
