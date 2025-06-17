package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
}