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

    /**
     * Payment method used for this transaction.
     * Common values: CASH, BANK_TRANSFER, CREDIT_CARD, ONLINE_PAYMENT, etc.
     */
    @Column(name = "pay_method", nullable = false, length = 20)
    private String payMethod;

    /**
     * Final amount actually paid by the student (after any discounts applied).
     * This represents the net payment amount received.
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Optional description or notes about this payment.
     * Can include transaction references, payment details, etc.
     */
    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    /**
     * Discount amount that was available/applied to this payment.
     * This helps track the original vs discounted amount.
     */
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
     * Original fee amount that was supposed to be paid (before any discounts).
     * This represents the base amount from the fee structure.
     * Used to calculate effective discounts: generatedAmount - amount = discount applied.
     */
    @Column(name = "generated_amount", precision = 10, scale = 2)
    private BigDecimal generatedAmount;



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
     * 
     * Business Logic:
     * - amount: Final amount actually paid by student (after any discounts)
     * - generatedAmount: Original amount that was supposed to be paid (before discounts)
     * - haveDiscount: Available discount amount
     * - Each PaymentDetail record represents an actual payment made
     */
    public void updatePaymentStatus() {
        if (amount == null) {
            paymentStatus = PaymentStatus.PENDING;
            return;
        }

        // If amount > 0, it means a payment has been made
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            // Calculate the expected amount (original - discount)
            BigDecimal expectedAmount = generatedAmount != null ? generatedAmount : amount;
            BigDecimal discountAmount = haveDiscount != null ? haveDiscount : BigDecimal.ZERO;
            BigDecimal requiredAmount = expectedAmount.subtract(discountAmount);
            
            // Check if payment meets or exceeds the required amount
            if (amount.compareTo(requiredAmount) >= 0) {
                paymentStatus = PaymentStatus.PAID;
            } else {
                // Partial payment made
                if (isOverdue()) {
                    paymentStatus = PaymentStatus.OVERDUE;
                } else {
                    paymentStatus = PaymentStatus.PARTIAL;
                }
            }
        } else {
            // No payment made yet
            if (isOverdue()) {
                paymentStatus = PaymentStatus.OVERDUE;
            } else {
                paymentStatus = PaymentStatus.PENDING;
            }
        }
    }
}