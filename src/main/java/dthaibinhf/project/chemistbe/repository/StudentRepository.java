package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    @Query("SELECT s FROM Student s " +
            "LEFT JOIN FETCH s.scores " +
            "LEFT JOIN FETCH s.attendances a " +
            "LEFT JOIN FETCH a.schedule " +
            "LEFT JOIN FETCH s.paymentDetails " +
            "LEFT JOIN FETCH s.studentDetails sd " +
            "LEFT JOIN FETCH sd.group " +
            "LEFT JOIN FETCH sd.school " +
            "LEFT JOIN FETCH sd.schoolClass " +
            "LEFT JOIN FETCH sd.academicYear " +
            "LEFT JOIN FETCH sd.grade " +
            "WHERE s.endAt IS NULL")
    List<Student> findAllActive();

    @Query("SELECT s FROM Student s " +
            "LEFT JOIN FETCH s.scores " +
            "LEFT JOIN FETCH s.attendances a " +
            "LEFT JOIN FETCH a.schedule " +
            "LEFT JOIN FETCH s.paymentDetails " +
            "LEFT JOIN FETCH s.studentDetails " +
            "WHERE s.id = :id AND s.endAt IS NULL")
    Optional<Student> findActiveById(@Param("id") Integer id);

    /**
     * Find students by group ID with their newest non-deleted student details
     * 
     * @param groupId the ID of the group
     * @return list of students with their newest non-deleted student details for the specified group
     */
    @Query("SELECT DISTINCT s FROM Student s " +
           "JOIN FETCH s.studentDetails sd " +
           "WHERE sd.group.id = :groupId " +
           "AND sd.endAt IS NULL " +
           "AND s.endAt IS NULL " +
           "AND sd.createdAt = (SELECT MAX(sd2.createdAt) FROM StudentDetail sd2 " +
           "                    WHERE sd2.student.id = s.id " +
           "                    AND sd2.group.id = :groupId " +
           "                    AND sd2.endAt IS NULL)")
    List<Student> findByGroupIdWithNewestActiveDetails(@Param("groupId") Integer groupId);
}
