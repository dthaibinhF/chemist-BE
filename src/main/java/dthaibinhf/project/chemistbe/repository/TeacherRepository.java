package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    @Query("SELECT t FROM Teacher t WHERE t.id = :id AND (t.endAt IS NULL OR t.endAt > CURRENT_TIMESTAMP)")
    Optional<Teacher> findActiveById(@Param("id") Integer id);

    @Query("SELECT t FROM Teacher t WHERE t.endAt IS NULL OR t.endAt > CURRENT_TIMESTAMP")
    List<Teacher> findAllActiveTeachers();

    @Query("SELECT DISTINCT t FROM Teacher t " +
           "LEFT JOIN t.account acc " +
           "WHERE (t.endAt IS NULL OR t.endAt > CURRENT_TIMESTAMP) " +
           "AND (:teacherName IS NULL OR LOWER(acc.name) LIKE LOWER(:teacherName)) " +
           "AND (:phone IS NULL OR acc.phone LIKE :phone) " +
           "AND (:email IS NULL OR LOWER(acc.email) LIKE LOWER(:email))")
    Page<Teacher> searchTeachers(@Param("teacherName") String teacherName,
                                @Param("phone") String phone,
                                @Param("email") String email,
                                Pageable pageable);
}