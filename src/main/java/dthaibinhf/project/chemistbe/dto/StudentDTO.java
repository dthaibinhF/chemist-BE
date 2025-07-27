package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Student}
 */
@Value
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({ "id", "name", "parent_phone", "scores", "attendances", "payment_details", "student_details" })
public class StudentDTO extends BaseDTO implements Serializable {
    String name;
    @JsonProperty("parent_phone")
    String parentPhone;
    Set<ScoreDTO> scores;
    Set<AttendanceDTO> attendances;
    @JsonProperty("payment_details")
    Set<PaymentDetailDTO> paymentDetails;
    @JsonProperty("student_details")
    Set<StudentDetailDTO> studentDetails;
}