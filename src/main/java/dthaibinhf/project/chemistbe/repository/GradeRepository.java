package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {
}