package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Integer> {
    @Query("SELECT sc FROM SchoolClass sc WHERE sc.id = :id AND (sc.endAt IS NULL OR sc.endAt > CURRENT_TIMESTAMP)")
    Optional<SchoolClass> findActiveById(@Param("id") Integer id);

    @Query("SELECT sc FROM SchoolClass sc WHERE sc.endAt IS NULL OR sc.endAt > CURRENT_TIMESTAMP")
    List<SchoolClass> findAllActiveSchoolClasses();

    @Query("SELECT sc FROM SchoolClass sc WHERE (sc.endAt IS NULL OR sc.endAt > CURRENT_TIMESTAMP) AND sc.name LIKE CONCAT(:gradePrefix, '%')")
    List<SchoolClass> findAllActiveByGrade(@Param("gradePrefix") Integer gradePrefix);

}