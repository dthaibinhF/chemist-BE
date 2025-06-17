package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.StudentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentDetailRepository extends JpaRepository<StudentDetail, Integer> {
}