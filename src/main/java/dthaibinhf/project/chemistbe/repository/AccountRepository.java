package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Query("select a from Account a JOIN FETCH a.role WHERE a.email = ?1")
    Optional<Account> findByEmail(String username);

    @Query("SELECT a FROM Account a WHERE a.id = :id AND (a.endAt IS NULL OR a.endAt > CURRENT_TIMESTAMP)")
    Optional<Account> findActiveById(@Param("id") Integer id);

    @Query("SELECT a FROM Account a WHERE a.endAt IS NULL OR a.endAt > CURRENT_TIMESTAMP")
    List<Account> findAllActiveAccounts();

    boolean existsByRoleId(Integer roleId);
}