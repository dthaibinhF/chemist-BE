package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for dashboard statistics
 */
@Value
@Builder
public class StatisticsDTO implements Serializable {
    
    @JsonProperty("total_students")
    Long totalStudents;
    
    @JsonProperty("active_students")
    Long activeStudents;
    
    @JsonProperty("total_teachers")
    Long totalTeachers;
    
    @JsonProperty("active_teachers")
    Long activeTeachers;
    
    @JsonProperty("total_groups")
    Long totalGroups;
    
    @JsonProperty("active_groups")
    Long activeGroups;
    
    @JsonProperty("total_schedules")
    Long totalSchedules;
    
    @JsonProperty("this_week_schedules")
    Long thisWeekSchedules;
    
    @JsonProperty("total_attendances")
    Long totalAttendances;
    
    @JsonProperty("attendance_rate_percentage")
    BigDecimal attendanceRatePercentage;
}