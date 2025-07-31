package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Integer> {
    @Query("SELECT f FROM Fee f WHERE f.id = :id AND (f.endAt IS NULL OR f.endAt > CURRENT_TIMESTAMP)")
    Optional<Fee> findActiveById(@Param("id") Integer id);

    @Query("SELECT f FROM Fee f WHERE f.endAt IS NULL OR f.endAt > CURRENT_TIMESTAMP")
    List<Fee> findAllActiveFees();

    @Query("SELECT f FROM Fee f JOIN f.groups g WHERE g.id = :groupId AND (f.endTime IS NULL OR f.endAt > CURRENT_TIMESTAMP)")
    Optional<Fee> findCurrentFeeOfGroup();

}