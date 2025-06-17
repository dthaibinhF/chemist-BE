package dthaibinhf.project.chemistbe.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Teacher}
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class TeacherDTO extends BaseDTO implements Serializable {
    AccountDTO account;
    Set<TeacherDetailDTO> teacherDetails;
}