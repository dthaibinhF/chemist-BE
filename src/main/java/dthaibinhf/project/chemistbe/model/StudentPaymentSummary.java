package dthaibinhf.project.chemistbe.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Entity representing a summary of payment obligations for a student.
 * 
 * This entity tracks the overall payment status for a student's enrollment in a specific
 * group/fee/academic year combination. It aggregates information from multiple PaymentDetail
 * records to provide a comprehensive view of what the student owes and has paid.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "student_payment_summary",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_student_payment_summary_unique", 
                           columnNames = {"student_id", "fee_id", "academic_year_id", "group_id"})
       })
public class StudentPaymentSummary extends BaseEntity {
    
    /**
     * The student this payment summary belongs to.
     * Required field.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonBackReference
    private Student student;
    
    /**
     * The fee structure this payment summary is based on.
     * Required field.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fee_id", nullable = false)
    @JsonBackReference
    private Fee fee;
    
    /**
     * The academic year for this payment summary.
     * Required field.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false)
    @JsonBackReference
    private AcademicYear academicYear;
    
    /**
     * The group the student is enrolled in (optional for flexible fee structures).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    @JsonBackReference
    private Group group;
    
    /**
     * Total amount the student is supposed to pay for this fee.
     * This may be different from the fee's base amount due to pro-rata calculations,
     * late enrollments, or other adjustments.
     */
    @Column(name = "total_amount_due", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalAmountDue = BigDecimal.ZERO;
    
    /**
     * Total amount the student has actually paid for this fee.
     * This is calculated by summing all related PaymentDetail records.
     */
    @Column(name = "total_amount_paid", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalAmountPaid = BigDecimal.ZERO;
    
    /**
     * Outstanding amount that still needs to be paid.
     * Calculated as: totalAmountDue - totalAmountPaid
     */
    @Column(name = "outstanding_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal outstandingAmount = BigDecimal.ZERO;
    
    /**
     * Current payment status for this fee obligation.
     * Automatically calculated based on payment amounts and due dates.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    /**
     * Date when the payment is due.
     * If null, there is no specific due date (payment can be made anytime).
     */
    @Column(name = "due_date")
    private OffsetDateTime dueDate;
    
    /**
     * Date when the student enrolled in the group/fee.
     * Used for pro-rata calculations and payment history tracking.
     */
    @Column(name = "enrollment_date", nullable = false)
    private OffsetDateTime enrollmentDate;
    
    /**
     * Calculate and update the outstanding amount and payment status.
     * This method should be called whenever payment amounts change.
     */
    public void recalculateStatus() {
        // Calculate outstanding amount
        this.outstandingAmount = this.totalAmountDue.subtract(this.totalAmountPaid);
        
        // Determine payment status
        if (this.totalAmountPaid.compareTo(BigDecimal.ZERO) == 0) {
            // No payment made
            if (isDueDate()) {
                this.paymentStatus = PaymentStatus.OVERDUE;
            } else {
                this.paymentStatus = PaymentStatus.PENDING;
            }
        } else if (this.outstandingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            // Fully paid or overpaid
            this.paymentStatus = PaymentStatus.PAID;
        } else {
            // Partially paid
            if (isDueDate()) {
                this.paymentStatus = PaymentStatus.OVERDUE;
            } else {
                this.paymentStatus = PaymentStatus.PARTIAL;
            }
        }
    }
    
    /**
     * Check if the payment is past due date.
     * @return true if due date has passed and payment is not complete
     */
    public boolean isOverdue() {
        return isDueDate() && this.outstandingAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Check if the payment has a due date that has passed.
     * @return true if due date exists and has passed
     */
    private boolean isDueDate() {
        return this.dueDate != null && OffsetDateTime.now().isAfter(this.dueDate);
    }
    
    /**
     * Check if the payment is fully paid.
     * @return true if payment is complete
     */
    public boolean isFullyPaid() {
        return this.paymentStatus == PaymentStatus.PAID;
    }
    
    /**
     * Get the payment completion percentage.
     * @return percentage of payment completed (0.0 to 1.0)
     */
    public BigDecimal getPaymentCompletionRate() {
        if (this.totalAmountDue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE; // If no amount due, consider it complete
        }
        return this.totalAmountPaid.divide(this.totalAmountDue, 4, java.math.RoundingMode.HALF_UP);
    }
}