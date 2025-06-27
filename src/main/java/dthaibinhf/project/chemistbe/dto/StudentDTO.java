package dthaibinhf.project.chemistbe.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

import dthaibinhf.project.chemistbe.model.StudentDetailDTO;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Student}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class StudentDTO extends BaseDTO implements Serializable {
    String name;
    String parentPhone;
    Set<ScoreDTO> scores;
    Set<AttendanceDTO> attendances;
    Set<PaymentDetailDTO> paymentDetails;
    Set<StudentDetailDTO> studentDetails;
}