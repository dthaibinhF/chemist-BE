package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Integer> {
}