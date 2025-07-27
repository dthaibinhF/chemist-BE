package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dthaibinhf.project.chemistbe.model.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.PaymentDetail}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class PaymentDetailDTO extends BaseDTO implements Serializable {
    @JsonProperty("fee_id")
    @NotNull
    Integer feeId;
    @JsonProperty("fee_name")
    String feeName;
    @JsonProperty("student_id")
    @NotNull
    Integer studentId;
    @JsonProperty("student_name")
    String studentName;
    @JsonProperty("pay_method")
    String payMethod;
    @NotNull @Positive
    BigDecimal amount;
    String description;
    @JsonProperty("have_discount")
    BigDecimal haveDiscount;
    
    /**
     * Current payment status.
     */
    @NotNull
    @JsonProperty("payment_status")
    PaymentStatus paymentStatus;
    
    /**
     * Date when this payment is due.
     */
    @JsonProperty("due_date")
    OffsetDateTime dueDate;
    
    /**
     * Original amount before discounts.
     */
    @JsonProperty("generated_amount")
    BigDecimal generatedAmount;
    
    
    /**
     * Whether this payment is overdue.
     */
    @JsonProperty("is_overdue")
    Boolean isOverdue;
}