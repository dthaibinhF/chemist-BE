package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.ScoreDTO;
import dthaibinhf.project.chemistbe.mapper.ScoreMapper;
import dthaibinhf.project.chemistbe.model.Score;
import dthaibinhf.project.chemistbe.repository.ExamRepository;
import dthaibinhf.project.chemistbe.repository.ScoreRepository;
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
public class ScoreService {
    ScoreRepository scoreRepository;
    ScoreMapper scoreMapper;
    ExamRepository examRepository;
    StudentRepository studentRepository;

    public List<ScoreDTO> getAllScores() {
        return scoreRepository.findAll().stream()
                .map(scoreMapper::toDto)
                .collect(Collectors.toList());
    }

    public ScoreDTO getScoreById(Integer id) {
        Score score = scoreRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Score not found: " + id));
        return scoreMapper.toDto(score);
    }

    @Transactional
    public ScoreDTO createScore(ScoreDTO scoreDTO) {
        Score score = scoreMapper.toEntity(scoreDTO);
        score.setId(null);
        setRelatedEntities(score, scoreDTO);
        Score saved = scoreRepository.save(score);
        return scoreMapper.toDto(saved);
    }

    @Transactional
    public ScoreDTO updateScore(Integer id, ScoreDTO scoreDTO) {
        Score score = scoreRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Score not found: " + id));
        scoreMapper.partialUpdate(scoreDTO, score);
        setRelatedEntities(score, scoreDTO);
        Score updated = scoreRepository.save(score);
        return scoreMapper.toDto(updated);
    }

    @Transactional
    public void deleteScore(Integer id) {
        Score score = scoreRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Score not found: " + id));
        score.softDelete();
        scoreRepository.save(score);
    }

    private void setRelatedEntities(Score score, ScoreDTO scoreDTO) {
        score.setExam(examRepository.findById(scoreDTO.getExamId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found: " + scoreDTO.getExamId())));
        score.setStudent(studentRepository.findById(scoreDTO.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + scoreDTO.getStudentId())));
    }

    public List<ScoreDTO> getScoresByExamId(Integer examId) {
        return scoreRepository.findAll().stream()
                .filter(score -> score.getExam().getId().equals(examId))
                .map(scoreMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ScoreDTO> getScoresByStudentId(Integer studentId) {
        return scoreRepository.findAll().stream()
                .filter(score -> score.getStudent().getId().equals(studentId))
                .map(scoreMapper::toDto)
                .collect(Collectors.toList());
    }
}