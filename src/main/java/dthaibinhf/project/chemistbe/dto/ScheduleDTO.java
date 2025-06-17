package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("group_id")
    Integer groupId;
    @JsonProperty("group_name")
    String groupName;

    OffsetDateTime startTime;
    OffsetDateTime endTime;
    String deliveryMode;
    String meetingLink;
    List<AttendanceDTO> attendances;
    TeacherDTO teacher;
    RoomDTO room;
}