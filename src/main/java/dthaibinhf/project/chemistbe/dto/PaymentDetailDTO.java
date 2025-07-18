package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

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
}