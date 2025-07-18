package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.GroupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupScheduleRepository extends JpaRepository<GroupSchedule, Integer> {
    @Query("select grs FROM GroupSchedule grs WHERE grs.endAt IS NULL OR grs.endAt > CURRENT_TIMESTAMP")
    List<GroupSchedule> findAllActiveGroupSchedule();

    @Query("select grs FROM GroupSchedule grs WHERE grs.id = ?1 AND (grs.endAt IS NULL OR grs.endAt > CURRENT_TIMESTAMP)")
    Optional<GroupSchedule> findActiveById(Integer id);

    @Query("SELECT grs FROM GroupSchedule grs where grs.group.id = ?1 AND (grs.endAt IS NULL OR grs.endAt > CURRENT_TIMESTAMP)")
    List<GroupSchedule> findAllActiveByGroupId(Integer groupId);
}