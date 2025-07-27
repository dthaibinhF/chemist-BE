package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
   @Query("SELECT s FROM Student s " + "LEFT JOIN FETCH s.scores " + "LEFT JOIN FETCH s.attendances a "
         + "LEFT JOIN FETCH a.schedule " + "LEFT JOIN FETCH s.paymentDetails " + "LEFT JOIN FETCH s.studentDetails sd "
         + "LEFT JOIN FETCH sd.group " + "LEFT JOIN FETCH sd.school " + "LEFT JOIN FETCH sd.schoolClass "
         + "LEFT JOIN FETCH sd.academicYear " + "LEFT JOIN FETCH sd.grade " + "WHERE s.endAt IS NULL")
   List<Student> findAllActive();

   @Query("SELECT s FROM Student s " + "LEFT JOIN FETCH s.scores " + "LEFT JOIN FETCH s.attendances a "
         + "LEFT JOIN FETCH a.schedule " + "LEFT JOIN FETCH s.paymentDetails " + "LEFT JOIN FETCH s.studentDetails "
         + "WHERE s.id = :id AND s.endAt IS NULL")
   Optional<Student> findActiveById(@Param("id") Integer id);

   /**
    * Find students by group ID with their newest non-deleted student details
    *
    * @param groupId the ID of the group
    * @return list of students with their newest non-deleted student details for
    *         the specified group
    */
   @Query("SELECT DISTINCT s FROM Student s " + "JOIN FETCH s.studentDetails sd " + "WHERE sd.group.id = :groupId "
         + "AND sd.endAt IS NULL " + "AND s.endAt IS NULL "
         + "AND sd.createdAt = (SELECT MAX(sd2.createdAt) FROM StudentDetail sd2 "
         + "                    WHERE sd2.student.id = s.id " + "                    AND sd2.group.id = :groupId "
         + "                    AND sd2.endAt IS NULL)")
   List<Student> findByGroupIdWithNewestActiveDetails(@Param("groupId") Integer groupId);

   /**
    * Search students with pagination and sorting
    * Search by student ID, phone, name, group name, school name, or class name
    *
    * @param parentPhone search by parent phone (contains)
    * @param studentName search by student name (contains, case-insensitive)
    * @param groupName   search by group name (contains, case-insensitive)
    * @param schoolName  search by school name (contains, case-insensitive)
    * @param className   search by school class name (contains, case-insensitive)
    * @param pageable    pagination and sorting parameters
    * @return page of students matching the criteria
    */
   @Query(value = """
          SELECT DISTINCT s.* FROM student s\s
          LEFT JOIN student_detail sd ON s.id = sd.student_id\s
          LEFT JOIN "group" g ON sd.group_id = g.id\s
         LEFT JOIN school sc ON sd.school_id = sc.id\s
         LEFT JOIN school_class scl ON sd.school_class_id = scl.id\s
         WHERE (s.end_at IS NULL OR s.end_at > current_timestamp)\s
         AND (sd.end_at IS NULL OR sd.end_at > current_timestamp)\s
         and (g.end_at IS NULL OR g.end_at > current_timestamp)\s
         AND (scl.end_at IS NULL OR scl.end_at > current_timestamp)\s
         AND (:parentPhone IS NULL OR s.parent_phone LIKE LOWER(:parentPhone))\s
         AND (:studentName IS NULL OR LOWER(s.name) LIKE LOWER(:studentName))\s
         AND (:groupName IS NULL OR LOWER(g.name) LIKE LOWER(:groupName))\s
         AND (:schoolName IS NULL OR LOWER(sc.name) LIKE lower(:schoolName))\s
         AND (:className IS NULL OR LOWER(scl.name) LIKE LOWER(:className))\s
         """, nativeQuery = true)
   Page<Student> testSearchStudents(@Param("studentName") String studentName,
         @Param("groupName") String groupName,
         @Param("schoolName") String schoolName,
         @Param("className") String className,
         @Param("parentPhone") String parentPhone,
         Pageable pageable);
}
