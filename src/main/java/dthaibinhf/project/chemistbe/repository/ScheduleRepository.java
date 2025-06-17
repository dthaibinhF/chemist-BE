package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
}