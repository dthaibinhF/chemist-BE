package dthaibinhf.project.chemistbe.model;

/**
 * Enumeration for payment status tracking.
 * 
 * This enum defines the possible states of a payment throughout its lifecycle:
 * - PENDING: Payment has been generated but not yet paid
 * - PARTIAL: Payment has been partially paid (amount paid < amount due)
 * - PAID: Payment has been fully paid (amount paid >= amount due)
 * - OVERDUE: Payment is past due date and still not fully paid
 */
public enum PaymentStatus {
    /**
     * Payment obligation has been created but no payment has been made yet.
     * This is the default status when a payment record is first generated.
     */
    PENDING,
    
    /**
     * Some amount has been paid but the full amount is still outstanding.
     * This occurs when the total paid amount is greater than 0 but less than the total due amount.
     */
    PARTIAL,
    
    /**
     * The payment has been completed in full.
     * This occurs when the total paid amount equals or exceeds the total due amount.
     */
    PAID,
    
    /**
     * The payment is past its due date and has not been paid in full.
     * This status is automatically set by the system when checking for overdue payments.
     */
    OVERDUE
}