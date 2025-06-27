package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    @Query("SELECT a FROM Attendance a WHERE a.endAt IS NULL")
    List<Attendance> findAllActive();

    @Query("SELECT a FROM Attendance a WHERE a.id = :id AND a.endAt IS NULL")
    Optional<Attendance> findActiveById(@Param("id") Integer id);

    @Query("SELECT a FROM Attendance a WHERE a.endAt IS NULL AND (:groupId IS NULL OR a.schedule.group.id = :groupId) AND (:scheduleId IS NULL OR a.schedule.id = :scheduleId)")
    List<Attendance> findByGroupIdAndScheduleId(@Param("groupId") Integer groupId, @Param("scheduleId") Integer scheduleId);
}