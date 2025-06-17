package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Integer> {
}