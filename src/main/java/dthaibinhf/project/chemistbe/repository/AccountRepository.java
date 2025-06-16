package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    @Query("select a from Account a JOIN FETCH a.role WHERE a.email = ?1")
    Optional<Account> findByEmail(String username);
}