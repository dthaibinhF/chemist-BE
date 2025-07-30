package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

        @Query(value = """
                        SELECT s.* FROM schedule s
                        LEFT JOIN "group" g ON s.group_id = g.id
                        WHERE (s.end_at IS NULL OR s.end_at > current_timestamp)
                        AND (g.end_at IS NULL OR g.end_at > current_timestamp)
                        AND (:groupId IS NULL OR s.group_id = :groupId)
                        AND s.start_time >= COALESCE(:startDate, s.start_time)
                        AND s.end_time <= COALESCE(:endDate, s.end_time)
                        """, countQuery = """
                        SELECT COUNT(*) FROM schedule s
                        LEFT JOIN "group" g ON s.group_id = g.id
                        WHERE (s.end_at IS NULL OR s.end_at > current_timestamp)
                        AND (g.end_at IS NULL OR g.end_at > current_timestamp)
                        AND (:groupId IS NULL OR s.group_id = :groupId)
                        AND s.start_time >= COALESCE(:startDate, s.start_time)
                        AND s.end_time <= COALESCE(:endDate, s.end_time)
                        """, nativeQuery = true)
        Page<Schedule> findAllActivePageable(
                        @Param("groupId") Integer groupId,
                        @Param("startDate") OffsetDateTime startDate,
                        @Param("endDate") OffsetDateTime endDate,
                        Pageable pageable);

        @Query("SELECT COUNT(s) > 0 FROM Schedule s WHERE (s.endAt IS NULL OR s.endAt > CURRENT_TIMESTAMP) " +
                        "AND s.room.id = :roomId " +
                        "AND (" +
                        "(s.startTime >= :startTime AND s.startTime < :endTime) " +
                        "OR (s.endTime > :startTime AND s.endTime <= :endTime)  " +
                        "OR (s.startTime <= :startTime AND s.endTime >= :endTime)" +
                        ") " +
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

        /**
         * Find all active schedules for a specific teacher within a date range.
         * Used for salary calculation to count lessons taught in a specific month.
         * 
         * @param teacherId The ID of the teacher
         * @param startDateTime Start of the date range
         * @param endDateTime End of the date range
         * @return List of schedules for the teacher in the date range
         */
        @Query("SELECT s FROM Schedule s WHERE s.teacher.id = :teacherId " +
               "AND (s.endAt IS NULL OR s.endAt > CURRENT_TIMESTAMP) " +
               "AND s.startTime >= :startDateTime AND s.startTime <= :endDateTime " +
               "ORDER BY s.startTime")
        List<Schedule> findByTeacherIdAndDateRange(@Param("teacherId") Integer teacherId,
                                                  @Param("startDateTime") OffsetDateTime startDateTime,
                                                  @Param("endDateTime") OffsetDateTime endDateTime);

        /**
         * Find all active schedules for a specific group after a given date.
         * Used for finding future schedules with the same pattern for bulk updates.
         * 
         * @param groupId The ID of the group
         * @param afterDateTime Find schedules after this date/time
         * @return List of schedules for the group after the specified date
         */
        @Query("SELECT s FROM Schedule s WHERE s.group.id = :groupId " +
               "AND (s.endAt IS NULL OR s.endAt > CURRENT_TIMESTAMP) " +
               "AND s.startTime > :afterDateTime " +
               "ORDER BY s.startTime")
        List<Schedule> findActiveSchedulesByGroupIdAfterDate(@Param("groupId") Integer groupId,
                                                           @Param("afterDateTime") OffsetDateTime afterDateTime);
}