package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(String roleName);

    @Query("SELECT r FROM Role r WHERE r.name = :roleName AND (r.endAt IS NULL OR r.endAt > CURRENT_TIMESTAMP)")
    Optional<Role> findActiveByName(String roleName);

    @Query("SELECT r FROM Role r WHERE r.endAt IS NULL OR r.endAt > CURRENT_TIMESTAMP")
    List<Role> findAllActiveRoles();

    @Query("SELECT r FROM Role r WHERE r.id = :id AND (r.endAt IS NULL OR r.endAt > CURRENT_TIMESTAMP)")
    Optional<Role> findActiveById(Integer id);
}