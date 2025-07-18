package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Integer> {
    @Query("SELECT a FROM AcademicYear a WHERE a.id = :id AND (a.endAt IS NULL OR a.endAt > CURRENT_TIMESTAMP)")
    Optional<AcademicYear> findActiveById(@Param("id") Integer id);

    @Query("SELECT a FROM AcademicYear a WHERE a.endAt IS NULL OR a.endAt > CURRENT_TIMESTAMP")
    List<AcademicYear> findAllActiveAcademicYears();
}