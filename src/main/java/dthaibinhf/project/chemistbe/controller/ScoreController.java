package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.ScoreDTO;
import dthaibinhf.project.chemistbe.service.ScoreService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/score")
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ScoreController {
    ScoreService scoreService;

    @GetMapping
    public ResponseEntity<List<ScoreDTO>> getAllScores() {
        return ResponseEntity.ok(scoreService.getAllScores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScoreDTO> getScoreById(@PathVariable Integer id) {
        return ResponseEntity.ok(scoreService.getScoreById(id));
    }

    @PostMapping
    public ResponseEntity<ScoreDTO> createScore(@RequestBody ScoreDTO scoreDTO) {
        return ResponseEntity.ok(scoreService.createScore(scoreDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScoreDTO> updateScore(@PathVariable Integer id, @RequestBody ScoreDTO scoreDTO) {
        return ResponseEntity.ok(scoreService.updateScore(id, scoreDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScore(@PathVariable Integer id) {
        scoreService.deleteScore(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<ScoreDTO>> getScoresByExamId(@PathVariable Integer examId) {
        return ResponseEntity.ok(scoreService.getScoresByExamId(examId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ScoreDTO>> getScoresByStudentId(@PathVariable Integer studentId) {
        return ResponseEntity.ok(scoreService.getScoresByStudentId(studentId));
    }
}