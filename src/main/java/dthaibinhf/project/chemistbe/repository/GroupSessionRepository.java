package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.GroupSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupSessionRepository extends JpaRepository<GroupSession, Integer> {
}