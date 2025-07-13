package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.StudentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentDetailRepository extends JpaRepository<StudentDetail, Integer> {

    /**
     * Find the newest non-deleted student details for each student in a specific group
     * 
     * @param groupId the ID of the group
     * @return list of newest non-deleted student details for each student in the group
     */
    @Query("SELECT sd FROM StudentDetail sd " +
           "WHERE sd.group.id = :groupId " +
           "AND sd.endAt IS NULL " +
           "AND sd.createdAt = (SELECT MAX(sd2.createdAt) FROM StudentDetail sd2 " +
           "                    WHERE sd2.student.id = sd.student.id " +
           "                    AND sd2.group.id = :groupId " +
           "                    AND sd2.endAt IS NULL)")
    List<StudentDetail> findNewestActiveByGroupId(@Param("groupId") Integer groupId);

    /**
     * Find all student details for a specific student, ordered by creation date
     * 
     * @param studentId the ID of the student
     * @return list of all student details for the student, ordered by creation date (newest first)
     */
    @Query("SELECT sd FROM StudentDetail sd " +
           "WHERE sd.student.id = :studentId " +
           "ORDER BY sd.createdAt DESC")
    List<StudentDetail> findAllByStudentIdOrderByCreatedAtDesc(@Param("studentId") Integer studentId);
}
