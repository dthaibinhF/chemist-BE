package dthaibinhf.project.chemistbe.dto;

import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import dthaibinhf.project.chemistbe.model.StudentDetailDTO;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Student}
 */
@Value
@EqualsAndHashCode(callSuper = true)
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