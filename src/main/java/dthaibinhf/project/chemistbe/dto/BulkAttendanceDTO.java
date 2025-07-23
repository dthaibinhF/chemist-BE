package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for bulk attendance operations
 */
@Value
public class BulkAttendanceDTO implements Serializable {
    @JsonProperty("schedule_id")
    Integer scheduleId;
    
    @JsonProperty("attendance_records")
    List<AttendanceRecordDTO> attendanceRecords;
    
    @Value
    public static class AttendanceRecordDTO implements Serializable {
        @JsonProperty("student_id")
        Integer studentId;
        String status;
        String description;
    }
}