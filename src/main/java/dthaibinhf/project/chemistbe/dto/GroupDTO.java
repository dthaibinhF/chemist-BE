package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dthaibinhf.project.chemistbe.model.StudentDetailDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Group}
 */
@Value
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupDTO extends BaseDTO implements Serializable {

    @NotBlank
    String name;

    String level;

    @NotNull
    @JsonProperty("fee_id")
    Integer feeId;

    @JsonProperty("fee_name")
    String feeName;

    @NotNull
    @JsonProperty("academic_year_id")
    Integer academicYearId;

    @JsonProperty("academic_year")
    String academicYear;

    @NotNull
    @JsonProperty("grade_id")
    Integer gradeId;

    @JsonProperty("grade_name")
    String gradeName;

    @JsonProperty("group_schedules")
    Set<GroupScheduleDTO> groupSchedules;

    Set<ScheduleDTO> schedules;

    @JsonProperty("student_details")
    Set<StudentDetailDTO> studentDetails;
}