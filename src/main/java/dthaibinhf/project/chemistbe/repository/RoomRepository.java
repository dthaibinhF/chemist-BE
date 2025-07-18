package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    @Query("SELECT r FROM Room r WHERE r.id = :id AND (r.endAt IS NULL OR r.endAt > CURRENT_TIMESTAMP)")
    Optional<Room> findActiveById(@Param("id") Integer id);

    @Query("SELECT r FROM Room r WHERE r.endAt IS NULL OR r.endAt > CURRENT_TIMESTAMP")
    List<Room> findAllActiveRooms();
}