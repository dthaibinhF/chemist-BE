package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.PaymentStatus;
import dthaibinhf.project.chemistbe.model.StudentPaymentSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for StudentPaymentSummary entity.
 * Provides data access methods for student payment summary operations.
 */
@Repository
public interface StudentPaymentSummaryRepository extends JpaRepository<StudentPaymentSummary, Integer> {
    
    /**
     * Find all active student payment summaries (not soft deleted).
     * @return list of active student payment summaries
     */
    @Query("SELECT sps FROM StudentPaymentSummary sps WHERE sps.endAt IS NULL")
    List<StudentPaymentSummary> findAllActive();
    
    /**
     * Find active student payment summary by ID.
     * @param id the summary ID
     * @return optional student payment summary
     */
    @Query("SELECT sps FROM StudentPaymentSummary sps WHERE sps.id = :id AND sps.endAt IS NULL")
    Optional<StudentPaymentSummary> findActiveById(@Param("id") Integer id);
    
    /**
     * Find all active payment summaries for a specific student.
     * @param studentId the student ID
     * @return list of payment summaries for the student
     */
    @Query("SELECT sps FROM StudentPaymentSummary sps WHERE sps.student.id = :studentId AND sps.endAt IS NULL")
    List<StudentPaymentSummary> findActiveByStudentId(@Param("studentId") Integer studentId);
    
    /**
     * Find all active payment summaries for a specific fee.
     * @param feeId the fee ID
     * @return list of payment summaries for the fee
     */
    @Query("SELECT sps FROM StudentPaymentSummary sps WHERE sps.fee.id = :feeId AND sps.endAt IS NULL")
    List<StudentPaymentSummary> findActiveByFeeId(@Param("feeId") Integer feeId);
    
    /**
     * Find all active payment summaries for a specific group.
     * @param groupId the group ID
     * @return list of payment summaries for the group
     */
    @Query("SELECT sps FROM StudentPaymentSummary sps WHERE sps.group.id = :groupId AND sps.endAt IS NULL")
    List<StudentPaymentSummary> findActiveByGroupId(@Param("groupId") Integer groupId);
    
    /**
     * Find payment summary for a specific student, fee, academic year, and group combination.
     * @param studentId the student ID
     * @param feeId the fee ID
     * @param academicYearId the academic year ID
     * @param groupId the group ID (can be null)
     * @return optional payment summary
     */
    @Query("SELECT sps FROM StudentPaymentSummary sps WHERE " +
           "sps.student.id = :studentId AND sps.fee.id = :feeId AND " +
           "sps.academicYear.id = :academicYearId AND " +
           "(:groupId IS NULL AND sps.group IS NULL OR sps.group.id = :groupId) AND " +
           "sps.endAt IS NULL")
    Optional<StudentPaymentSummary> findActiveByStudentFeeAcademicYearAndGroup(
            @Param("studentId") Integer studentId,
            @Param("feeId") Integer feeId,
            @Param("academicYearId") Integer academicYearId,
            @Param("groupId") Integer groupId);
    
    /**
     * Find all active payment summaries with a specific payment status.
     * @param status the payment status
     * @return list of payment summaries with the specified status
     */
    @Query("SELECT sps FROM StudentPaymentSummary sps WHERE sps.paymentStatus = :status AND sps.endAt IS NULL")
    List<StudentPaymentSummary> findActiveByPaymentStatus(@Param("status") PaymentStatus status);
    
    /**
     * Find all active payment summaries that are overdue (past due date and not fully paid).
     * @param currentDate the current date to compare against
     * @return list of overdue payment summaries
     */
    @Query("SELECT sps FROM StudentPaymentSummary sps WHERE " +
           "sps.dueDate IS NOT NULL AND sps.dueDate < :currentDate AND " +
           "sps.paymentStatus IN ('PENDING', 'PARTIAL', 'OVERDUE') AND " +
           "sps.endAt IS NULL")
    List<StudentPaymentSummary> findOverduePayments(@Param("currentDate") OffsetDateTime currentDate);
    
    /**
     * Find all active payment summaries for a specific academic year.
     * @param academicYearId the academic year ID
     * @return list of payment summaries for the academic year
     */
    @Query("SELECT sps FROM StudentPaymentSummary sps WHERE sps.academicYear.id = :academicYearId AND sps.endAt IS NULL")
    List<StudentPaymentSummary> findActiveByAcademicYearId(@Param("academicYearId") Integer academicYearId);
    
    /**
     * Check if a payment summary exists for the given combination.
     * @param studentId the student ID
     * @param feeId the fee ID
     * @param academicYearId the academic year ID
     * @param groupId the group ID (can be null)
     * @return true if a summary exists
     */
    @Query("SELECT COUNT(sps) > 0 FROM StudentPaymentSummary sps WHERE " +
           "sps.student.id = :studentId AND sps.fee.id = :feeId AND " +
           "sps.academicYear.id = :academicYearId AND " +
           "(:groupId IS NULL AND sps.group IS NULL OR sps.group.id = :groupId) AND " +
           "sps.endAt IS NULL")
    boolean existsByStudentFeeAcademicYearAndGroup(
            @Param("studentId") Integer studentId,
            @Param("feeId") Integer feeId,
            @Param("academicYearId") Integer academicYearId,
            @Param("groupId") Integer groupId);
    
    /**
     * Find payment summaries with outstanding amounts greater than zero.
     * @return list of payment summaries with outstanding payments
     */
    @Query("SELECT sps FROM StudentPaymentSummary sps WHERE sps.outstandingAmount > 0 AND sps.endAt IS NULL")
    List<StudentPaymentSummary> findActiveWithOutstandingPayments();
    
    /**
     * Get total outstanding amount across all active payment summaries.
     * @return total outstanding amount
     */
    @Query("SELECT COALESCE(SUM(sps.outstandingAmount), 0) FROM StudentPaymentSummary sps WHERE sps.endAt IS NULL")
    java.math.BigDecimal getTotalOutstandingAmount();
    
    /**
     * Get total paid amount across all active payment summaries.
     * @return total paid amount
     */
    @Query("SELECT COALESCE(SUM(sps.totalAmountPaid), 0) FROM StudentPaymentSummary sps WHERE sps.endAt IS NULL")
    java.math.BigDecimal getTotalPaidAmount();

    /**
     * Find the active payment summary for a specific fee and student.
     * @param feeId the fee ID
     * @param studentId the student ID
     * @return optional payment summary
     */
    @Query("SELECT sps FROM StudentPaymentSummary sps WHERE sps.fee.id = :feeId AND sps.student.id = :studentId AND sps.endAt IS NULL")
    Optional<StudentPaymentSummary> findActiveByFeeAndStudent(Integer feeId, Integer studentId);
}