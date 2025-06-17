package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.GroupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupScheduleRepository extends JpaRepository<GroupSchedule, Integer> {
}