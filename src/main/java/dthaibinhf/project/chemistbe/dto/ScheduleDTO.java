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
 * DTO (Data Transfer Object) for {@link dthaibinhf.project.chemistbe.model.Schedule}
 * 
 * This class is used for transferring schedule data between layers, particularly
 * between the service layer and the controller layer. It contains all the necessary
 * fields to represent a schedule in the system, with appropriate JSON property mappings
 * for API responses.
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class ScheduleDTO extends BaseDTO implements Serializable {
    /**
     * The ID of the group associated with this schedule.
     * Required field, mapped to "group_id" in JSON.
     */
    @NotNull
    @JsonProperty("group_id")
    Integer groupId;

    /**
     * The name of the group associated with this schedule.
     * Mapped to "group_name" in JSON.
     */
    @JsonProperty("group_name")
    String groupName;

    /**
     * The start time of the schedule.
     * Required field.
     */
    @NotNull
    OffsetDateTime startTime;

    /**
     * The end time of the schedule.
     * Required field.
     */
    @NotNull
    OffsetDateTime endTime;

    /**
     * The mode of delivery for this schedule (e.g., "online", "in-person").
     * Required field, must not be blank.
     * Mapped to "delivery_mode" in JSON.
     */
    @NotBlank
    @JsonProperty("delivery_mode")
    String deliveryMode;

    /**
     * The meeting link for online sessions.
     * Optional field.
     * Mapped to "meeting_link" in JSON.
     */
    @JsonProperty("meeting_link")
    String meetingLink;

    /**
     * The list of attendance records associated with this schedule.
     */
    List<AttendanceDTO> attendances;

    /**
     * The teacher assigned to this schedule.
     * Optional field.
     */
    TeacherDTO teacher;

    /**
     * The room where this schedule takes place.
     * Optional field.
     */
    RoomDTO room;
}
