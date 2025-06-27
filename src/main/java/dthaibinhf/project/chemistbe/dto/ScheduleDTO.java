package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Schedule}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class ScheduleDTO extends BaseDTO implements Serializable {
    @NotNull
    @JsonProperty("group_id")
    Integer groupId;
    @JsonProperty("group_name")
    String groupName;
    @NotNull
    OffsetDateTime startTime;
    @NotNull
    OffsetDateTime endTime;
    @NotBlank
    String deliveryMode;
    String meetingLink;
    List<AttendanceDTO> attendances;
    TeacherDTO teacher;
    @NotNull
    RoomDTO room;
}