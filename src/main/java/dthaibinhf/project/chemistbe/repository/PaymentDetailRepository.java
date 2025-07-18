package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.PaymentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentDetailRepository extends JpaRepository<PaymentDetail, Integer> {
    // This interface extends JpaRepository to provide CRUD operations for PaymentDetail entities.
    // Additional custom query methods can be defined here if needed.

    @Query("SELECT p FROM PaymentDetail p WHERE p.id = :id AND (p.endAt IS NULL OR p.endAt > CURRENT_TIMESTAMP)")
    Optional<PaymentDetail> findActiveById(@Param("id") Integer id);

    @Query("SELECT p FROM PaymentDetail p WHERE p.endAt IS NULL OR p.endAt > CURRENT_TIMESTAMP")
    List<PaymentDetail> findAllActivePaymentDetails();

    @Query("SELECT p FROM PaymentDetail p WHERE p.student.id = :studentId AND (p.endAt IS NULL OR p.endAt > CURRENT_TIMESTAMP)")
    List<PaymentDetail> findActiveByStudentId(@Param("studentId") Integer studentId);

    @Query("SELECT p FROM PaymentDetail p WHERE p.fee.id = :feeId AND (p.endAt IS NULL OR p.endAt > CURRENT_TIMESTAMP)")
    List<PaymentDetail> findActiveByFeeId(@Param("feeId") Integer feeId);

    @Query("SELECT p FROM PaymentDetail p WHERE p.student.id = :studentId AND p.fee.id = :feeId AND (p.endAt IS NULL OR p.endAt > CURRENT_TIMESTAMP)")
    List<PaymentDetail> findActiveByStudentIdAndFeeId(@Param("studentId") Integer studentId, @Param("feeId") Integer feeId);
}