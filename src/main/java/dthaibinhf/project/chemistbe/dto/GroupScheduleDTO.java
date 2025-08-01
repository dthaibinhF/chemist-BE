package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dthaibinhf.project.chemistbe.model.GroupSchedule;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * DTO for {@link GroupSchedule}
 */
@Value
@EqualsAndHashCode(callSuper = true)
@JsonPropertyOrder({ "id", "group_id", "group_name", "day_of_week", "start_time", "end_time", "room_id", "room_name" })
public class GroupScheduleDTO extends BaseDTO implements Serializable {
    @JsonProperty("group_id")
    Integer groupId;
    @JsonProperty("group_name")
    String groupName;
    @JsonProperty("day_of_week")
    String dayOfWeek;
    @JsonProperty("start_time")
    LocalTime startTime;
    @JsonProperty("end_time")
    LocalTime endTime;
    @JsonProperty("room_id")
    Integer roomId;
    @JsonProperty("room_name")
    String roomName;
}