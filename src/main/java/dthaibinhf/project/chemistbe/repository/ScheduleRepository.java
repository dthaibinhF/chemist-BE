package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    @Query("SELECT s FROM Schedule s WHERE s.id = :id AND (s.endAt IS NULL OR s.endAt > CURRENT_TIMESTAMP)")
    Optional<Schedule> findActiveById(@Param("id") Integer id);

    @Query("SELECT s FROM Schedule s WHERE s.endAt IS NULL OR s.endAt > CURRENT_TIMESTAMP")
    List<Schedule> findAllActive();


//    @Query("SELECT s FROM Schedule s WHERE (s.endAt IS NULL OR s.endAt > CURRENT_TIMESTAMP) " +
//            "AND (:groupId IS NULL OR s.group.id = :groupId) "
//            "AND (:startDate IS NULL OR s.startTime >= :startDate) " +
//            "AND (:endDate IS NULL OR s.endTime <= :endDate)")
//    Page<Schedule> findAllActive(@Param("groupId") Integer groupId,
//                                 @Param("startDate") OffsetDateTime startDate,
//                                 @Param("endDate") OffsetDateTime endDate,
//                                 Pageable pageable);
//
    @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE (s.endAt IS NULL OR s.endAt > CURRENT_TIMESTAMP) " +
            "AND s.room.id = :roomId " +
            "AND ((s.startTime >= :startTime AND s.startTime < :endTime) OR " +
            "(s.endTime > :startTime AND s.endTime <= :endTime) OR " +
            "(s.startTime <= :startTime AND s.endTime >= :endTime)) " +
            "AND (:excludeId IS NULL OR s.id != :excludeId)")
    boolean existsRoomConflict(@Param("roomId") Integer roomId,
                               @Param("startTime") OffsetDateTime startTime,
                               @Param("endTime") OffsetDateTime endTime,
                               @Param("excludeId") Integer excludeId);

@Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE (s.endAt IS NULL OR s.endAt > CURRENT_TIMESTAMP) " +
        "AND s.teacher.id = :teacherId " +
        "AND ((s.startTime >= :startTime AND s.startTime < :endTime) OR " +
        "(s.endTime > :startTime AND s.endTime <= :endTime) OR " +
        "(s.startTime <= :startTime AND s.endTime >= :endTime)) " +
        "AND (:excludeId IS NULL OR s.id != :excludeId)")
boolean existsTeacherConflict(@Param("teacherId") Integer teacherId,
                              @Param("startTime") OffsetDateTime startTime,
                              @Param("endTime") OffsetDateTime endTime,
                              @Param("excludeId") Integer excludeId);
}