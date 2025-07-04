package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
}