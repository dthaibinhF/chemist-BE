package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.TeacherDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherDetailRepository extends JpaRepository<TeacherDetail, Integer> {
}