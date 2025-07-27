package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.PaymentDetail;
import dthaibinhf.project.chemistbe.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
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

    /**
     * Find all active payment details with a specific payment status.
     * @param status the payment status
     * @return list of payment details with the specified status
     */
    @Query("SELECT p FROM PaymentDetail p WHERE p.paymentStatus = :status AND (p.endAt IS NULL OR p.endAt > CURRENT_TIMESTAMP)")
    List<PaymentDetail> findActiveByPaymentStatus(@Param("status") PaymentStatus status);

    /**
     * Find all active payment details that are overdue.
     * @param currentDate the current date to compare against
     * @return list of overdue payment details
     */
    @Query("SELECT p FROM PaymentDetail p WHERE p.dueDate IS NOT NULL AND p.dueDate < :currentDate AND " +
           "p.paymentStatus IN ('PENDING', 'PARTIAL', 'OVERDUE') AND (p.endAt IS NULL OR p.endAt > CURRENT_TIMESTAMP)")
    List<PaymentDetail> findOverduePayments(@Param("currentDate") OffsetDateTime currentDate);

    /**
     * Get total amount paid for a specific student and fee combination.
     * @param studentId the student ID
     * @param feeId the fee ID
     * @return total amount paid
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentDetail p WHERE " +
           "p.student.id = :studentId AND p.fee.id = :feeId AND (p.endAt IS NULL OR p.endAt > CURRENT_TIMESTAMP)")
    java.math.BigDecimal getTotalAmountPaidByStudentAndFee(@Param("studentId") Integer studentId, @Param("feeId") Integer feeId);

    /**
     * Find all active payment details within a date range.
     * @param startDate the start date
     * @param endDate the end date
     * @return list of payment details within the date range
     */
    @Query("SELECT p FROM PaymentDetail p WHERE p.createdAt BETWEEN :startDate AND :endDate AND " +
           "(p.endAt IS NULL OR p.endAt > CURRENT_TIMESTAMP)")
    List<PaymentDetail> findActiveByDateRange(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    /**
     * Get total revenue within a date range.
     * @param startDate the start date
     * @param endDate the end date
     * @return total revenue amount
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM PaymentDetail p WHERE " +
           "p.createdAt BETWEEN :startDate AND :endDate AND (p.endAt IS NULL OR p.endAt > CURRENT_TIMESTAMP)")
    java.math.BigDecimal getTotalRevenueByDateRange(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);
}