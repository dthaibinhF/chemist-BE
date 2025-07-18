package dthaibinhf.project.chemistbe.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dthaibinhf.project.chemistbe.dto.*;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link StudentDetail}
 */
@Value
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({"id", "student_name","school", "group_id", "group_name", "class", "academic_year", "grade", "student_id"})
public class StudentDetailDTO extends BaseDTO implements Serializable {

    @JsonProperty("group_id")
    Integer groupId;
    @JsonProperty("group_name")
    String groupName;
    SchoolDTO school;
    @JsonProperty("class")
    SchoolClassDTO schoolClass;
    @JsonProperty("academic_year")
    AcademicYearDTO academicYear;
    GradeDTO grade;
    @JsonProperty("student_id")
    Integer studentId;
    @JsonProperty("student_name")
    String studentName;
}