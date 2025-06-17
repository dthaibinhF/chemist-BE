package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
}