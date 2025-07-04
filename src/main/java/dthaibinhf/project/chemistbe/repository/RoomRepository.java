package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
}