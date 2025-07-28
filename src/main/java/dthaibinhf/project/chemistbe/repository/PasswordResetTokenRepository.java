package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {
    
    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.email = :email AND (prt.endAt IS NULL OR prt.endAt > CURRENT_TIMESTAMP) ORDER BY prt.createdAt DESC")
    List<PasswordResetToken> findActiveByEmail(@Param("email") String email);
    
    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.email = :email AND prt.otpCode = :otpCode AND (prt.endAt IS NULL OR prt.endAt > CURRENT_TIMESTAMP)")
    Optional<PasswordResetToken> findActiveByEmailAndOtpCode(@Param("email") String email, @Param("otpCode") String otpCode);
    
    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.id = :id AND (prt.endAt IS NULL OR prt.endAt > CURRENT_TIMESTAMP)")
    Optional<PasswordResetToken> findActiveById(@Param("id") Integer id);
    
    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.endAt IS NULL OR prt.endAt > CURRENT_TIMESTAMP")
    List<PasswordResetToken> findAllActive();
    
    @Query("SELECT prt FROM PasswordResetToken prt WHERE prt.expiresAt < :currentTime AND (prt.endAt IS NULL OR prt.endAt > CURRENT_TIMESTAMP)")
    List<PasswordResetToken> findExpiredTokens(@Param("currentTime") OffsetDateTime currentTime);
    
    @Query("SELECT COUNT(prt) FROM PasswordResetToken prt WHERE prt.email = :email AND prt.createdAt > :since AND (prt.endAt IS NULL OR prt.endAt > CURRENT_TIMESTAMP)")
    long countRecentRequestsByEmail(@Param("email") String email, @Param("since") OffsetDateTime since);
}