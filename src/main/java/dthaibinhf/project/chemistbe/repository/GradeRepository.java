package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {
    @Query("SELECT g FROM Grade g WHERE g.id = :id AND (g.endAt IS NULL OR g.endAt > CURRENT_TIMESTAMP)")
    Optional<Grade> findActiveById(@Param("id") Integer id);

    @Query("SELECT g FROM Grade g WHERE g.endAt IS NULL OR g.endAt > CURRENT_TIMESTAMP")
    List<Grade> findAllActiveGrades();
}