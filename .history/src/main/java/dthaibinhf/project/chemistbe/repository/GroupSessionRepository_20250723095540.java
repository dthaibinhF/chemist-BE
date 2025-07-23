package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.GroupSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupSessionRepository extends JpaRepository<GroupSession, Integer> {
    
    @Query("SELECT gs FROM GroupSession gs WHERE gs.endAt IS NULL AND gs.id = :id")
    Optional<GroupSession> findActiveById(@Param("id") Integer id);
    
    @Query("SELECT gs FROM GroupSession gs WHERE gs.endAt IS NULL ORDER BY gs.date DESC, gs.startTime DESC")
    List<GroupSession> findAllActive();
}