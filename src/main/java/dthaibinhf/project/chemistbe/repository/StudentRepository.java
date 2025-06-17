package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
}