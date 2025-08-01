package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dthaibinhf.project.chemistbe.model.AcademicYear;
import dthaibinhf.project.chemistbe.model.Fee;
import dthaibinhf.project.chemistbe.model.Grade;
import dthaibinhf.project.chemistbe.model.Group;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

/**
* {@link Group}DTO for fetching {@link Fee}, {@link Grade}, {@link AcademicYear}.
 * Used for list operations to reduce payload.
*/
@Value
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL) // Skip null fields
@JsonPropertyOrder({"id", "name", "level", "fee_id", "fee_name", "academic_year_id", "academic_year", "grade_id", "grade_name", "group_schedules"})
public class GroupListDTO extends BaseDTO implements Serializable {

    @NotBlank @Size(max = 50)
    String name;

    @Size(max = 20)
    String level;

    @NotNull
    @JsonProperty("fee_id")
    Integer feeId;

    @NotBlank
    @JsonProperty("fee_name")
    String feeName;

    @NotNull
    @JsonProperty("academic_year_id")
    Integer academicYearId;

    @NotBlank
    @JsonProperty("academic_year")
    String academicYear;

    @NotNull
    @JsonProperty("grade_id")
    Integer gradeId;

    @NotBlank
    @JsonProperty("grade_name")
    String gradeName;

    @JsonProperty("group_schedules")
    Set<GroupScheduleDTO> groupSchedules;
}