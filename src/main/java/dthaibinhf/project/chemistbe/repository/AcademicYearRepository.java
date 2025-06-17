package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.AcademicYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicYearRepository extends JpaRepository<AcademicYear, Integer> {
}