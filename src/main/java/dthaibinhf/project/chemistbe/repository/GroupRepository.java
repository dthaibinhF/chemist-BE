package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    @Query("SELECT g FROM Group g " +
           "LEFT JOIN FETCH g.fee " +
           "LEFT JOIN FETCH g.academicYear " +
           "LEFT JOIN FETCH g.grade " +
           "LEFT JOIN FETCH g.groupSchedules gs " +
           "LEFT JOIN FETCH gs.room " +
           "WHERE g.id = :id AND (g.endAt IS NULL OR g.endAt > CURRENT_TIMESTAMP)")
    Optional<Group> findActiveById(@Param("id") Integer id);


    @Query("SELECT g FROM Group g " +
           "LEFT JOIN FETCH g.fee " +
           "LEFT JOIN FETCH g.academicYear " +
           "LEFT JOIN FETCH g.grade " +
           "LEFT JOIN FETCH g.groupSchedules gc " +
           "LEFT JOIN FETCH gc.room " +
           "WHERE g.endAt IS NULL OR g.endAt > CURRENT_TIMESTAMP")
    List<Group> findAllActiveGroups();

    @Query("SELECT g FROM Group g " +
           "LEFT JOIN FETCH g.fee " +
           "LEFT JOIN FETCH g.academicYear " +
           "LEFT JOIN FETCH g.grade " +
           "LEFT JOIN FETCH g.groupSchedules gc " +
           "LEFT JOIN FETCH gc.room " +
           "WHERE g.academicYear.id = :academicYearId AND (g.endAt IS NULL OR g.endAt > CURRENT_TIMESTAMP)")
    List<Group> findActiveByAcademicYearId(@Param("academicYearId") Integer academicYearId);

    @Query("SELECT g FROM Group g " +
           "LEFT JOIN FETCH g.fee " +
           "LEFT JOIN FETCH g.academicYear " +
           "LEFT JOIN FETCH g.grade " +
           "LEFT JOIN FETCH g.groupSchedules gc " +
           "LEFT JOIN FETCH gc.room " +
           "WHERE g.grade.id = :gradeId AND (g.endAt IS NULL OR g.endAt > CURRENT_TIMESTAMP)")
    List<Group> findActiveByGradeId(@Param("gradeId") Integer gradeId);

    @Query("SELECT g FROM Group g " +
           "LEFT JOIN FETCH g.fee " +
           "LEFT JOIN FETCH g.academicYear " +
           "LEFT JOIN FETCH g.grade " +
           "LEFT JOIN FETCH g.groupSchedules gc " +
           "LEFT JOIN FETCH gc.room " +
           "WHERE g.name LIKE :groupName AND (g.endAt IS NULL OR g.endAt > CURRENT_TIMESTAMP)")
    Optional<Group> findActiveByGroupName(String groupName);
}