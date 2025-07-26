package dthaibinhf.project.chemistbe.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "payment_detail")
public class PaymentDetail extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fee_id", nullable = false)
    @JsonBackReference
    private Fee fee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonBackReference
    private Student student;

    @Column(name = "pay_method", nullable = false, length = 20)
    private String payMethod;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "have_discount", precision = 10, scale = 2)
    private BigDecimal haveDiscount;

    /**
     * Current status of this payment record.
     * Automatically calculated based on payment amounts and due dates.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    /**
     * Date when this payment is due.
     * If null, there is no specific due date for this payment.
     */
    @Column(name = "due_date")
    private OffsetDateTime dueDate;

    /**
     * Original amount that was supposed to be paid before any discounts.
     * This helps track the full fee amount vs actual payment amount.
     */
    @Column(name = "generated_amount", precision = 10, scale = 2)
    private BigDecimal generatedAmount;


    /**
     * Calculate the effective discount applied to this payment.
     * @return discount amount (generatedAmount - amount)
     */
    public BigDecimal getEffectiveDiscount() {
        if (generatedAmount == null || amount == null) {
            return haveDiscount != null ? haveDiscount : BigDecimal.ZERO;
        }
        return generatedAmount.subtract(amount);
    }

    /**
     * Check if this payment is overdue.
     * @return true if due date has passed and payment status is not PAID
     */
    public boolean isOverdue() {
        return dueDate != null && 
               OffsetDateTime.now().isAfter(dueDate) && 
               paymentStatus != PaymentStatus.PAID;
    }

    /**
     * Update payment status based on current conditions.
     * This method should be called when payment amounts or dates change.
     */
    public void updatePaymentStatus() {
        if (amount == null || generatedAmount == null) {
            paymentStatus = PaymentStatus.PENDING;
            return;
        }

        if (amount.compareTo(generatedAmount) >= 0) {
            paymentStatus = PaymentStatus.PAID;
        } else if (amount.compareTo(BigDecimal.ZERO) > 0) {
            if (isOverdue()) {
                paymentStatus = PaymentStatus.OVERDUE;
            } else {
                paymentStatus = PaymentStatus.PARTIAL;
            }
        } else {
            if (isOverdue()) {
                paymentStatus = PaymentStatus.OVERDUE;
            } else {
                paymentStatus = PaymentStatus.PENDING;
            }
        }
    }
}