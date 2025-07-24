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

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Attendance a WHERE a.schedule.id = :scheduleId AND a.student.id = :studentId AND a.endAt IS NULL")
    boolean existsByScheduleIdAndStudentIdAndDeletedFalse(@Param("scheduleId") Integer scheduleId, @Param("studentId") Integer studentId);

    @Query("SELECT a FROM Attendance a WHERE a.schedule.id = :scheduleId AND a.student.id = :studentId AND a.endAt IS NULL")
    List<Attendance> findByScheduleIdAndStudentIdAndDeletedFalse(@Param("scheduleId") Integer scheduleId, @Param("studentId") Integer studentId);
}