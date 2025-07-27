package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dthaibinhf.project.chemistbe.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.StudentPaymentSummary}
 * 
 * This DTO represents a summary of payment obligations for a student's enrollment
 * in a specific group/fee/academic year combination.
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class StudentPaymentSummaryDTO extends BaseDTO implements Serializable {
    
    /**
     * ID of the student this summary belongs to.
     */
    @NotNull
    @JsonProperty("student_id")
    Integer studentId;
    
    /**
     * Name of the student for display purposes.
     */
    @JsonProperty("student_name")
    String studentName;
    
    /**
     * ID of the fee structure this summary is based on.
     */
    @NotNull
    @JsonProperty("fee_id")
    Integer feeId;
    
    /**
     * Name of the fee for display purposes.
     */
    @JsonProperty("fee_name")
    String feeName;
    
    /**
     * ID of the academic year for this summary.
     */
    @NotNull
    @JsonProperty("academic_year_id")
    Integer academicYearId;
    
    /**
     * Name of the academic year for display purposes.
     */
    @JsonProperty("academic_year_name")
    String academicYearName;
    
    /**
     * ID of the group (optional for flexible fee structures).
     */
    @JsonProperty("group_id")
    Integer groupId;
    
    /**
     * Name of the group for display purposes.
     */
    @JsonProperty("group_name")
    String groupName;
    
    /**
     * Total amount the student is supposed to pay for this fee.
     */
    @NotNull
    @PositiveOrZero
    @JsonProperty("total_amount_due")
    BigDecimal totalAmountDue;
    
    /**
     * Total amount the student has actually paid for this fee.
     */
    @NotNull
    @PositiveOrZero
    @JsonProperty("total_amount_paid")
    BigDecimal totalAmountPaid;
    
    /**
     * Outstanding amount that still needs to be paid.
     */
    @NotNull
    @JsonProperty("outstanding_amount")
    BigDecimal outstandingAmount;
    
    /**
     * Current payment status for this fee obligation.
     */
    @NotNull
    @JsonProperty("payment_status")
    PaymentStatus paymentStatus;
    
    /**
     * Date when the payment is due.
     */
    @JsonProperty("due_date")
    OffsetDateTime dueDate;
    
    /**
     * Date when the student enrolled in the group/fee.
     */
    @NotNull
    @JsonProperty("enrollment_date")
    OffsetDateTime enrollmentDate;
    
    /**
     * Payment completion percentage (0.0 to 1.0).
     */
    @JsonProperty("completion_rate")
    BigDecimal completionRate;
    
    /**
     * Whether the payment is overdue.
     */
    @JsonProperty("is_overdue")
    Boolean isOverdue;
    
    /**
     * Whether the payment is fully paid.
     */
    @JsonProperty("is_fully_paid")
    Boolean isFullyPaid;
}