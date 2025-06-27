package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Teacher;
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
}