package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {
    @Query("SELECT e FROM Exam e WHERE e.id = :id AND (e.endAt IS NULL OR e.endAt > CURRENT_TIMESTAMP)")
    Optional<Exam> findActiveById(@Param("id") Integer id);

    @Query("SELECT e FROM Exam e WHERE e.endAt IS NULL OR e.endAt > CURRENT_TIMESTAMP")
    List<Exam> findAllActiveExams();
}