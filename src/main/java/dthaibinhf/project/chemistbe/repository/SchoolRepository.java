package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {
}