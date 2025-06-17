package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Integer> {
}