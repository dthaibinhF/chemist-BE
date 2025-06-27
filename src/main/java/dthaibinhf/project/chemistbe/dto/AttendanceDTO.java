package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import dthaibinhf.project.chemistbe.model.Attendance;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Attendance}
 */
@Value
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({
    "id",
    "schedule_id",
    "group_id",
    "group_name",
    "student_id",
    "student_name",
    "status",
    "description"
})
public class AttendanceDTO extends BaseDTO implements Serializable {
    @JsonProperty("schedule_id")
    Integer scheduleId;
    @JsonProperty("group_id")
    Integer groupId;
    @JsonProperty("group_name")
    String groupName;
    @JsonProperty("student_id")
    Integer studentId;
    @JsonProperty("student_name")
    String studentName;
    String status;
    String description;
}