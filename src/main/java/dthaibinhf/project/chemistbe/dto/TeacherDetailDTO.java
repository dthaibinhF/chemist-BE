package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.TeacherDetail}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class TeacherDetailDTO extends BaseDTO implements Serializable {

    @JsonProperty("teacher_id")
    Integer teacherId;
    @JsonProperty("teacher_name")
    String teacherName;
    SchoolDTO school;
    SchoolClassDTO schoolClass;
}