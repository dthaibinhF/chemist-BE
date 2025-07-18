package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Integer> {
    Optional<School> findSchoolByName(String name);

    @Query("SELECT s FROM School s WHERE s.id = :id AND (s.endAt IS NULL OR s.endAt > CURRENT_TIMESTAMP)")
    Optional<School> findActiveById(@Param("id") Integer id);

    @Query("SELECT s FROM School s WHERE s.endAt IS NULL OR s.endAt > CURRENT_TIMESTAMP")
    List<School> findAllActiveSchools();
}