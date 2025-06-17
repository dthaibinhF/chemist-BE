package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Integer> {
}