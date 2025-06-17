package dthaibinhf.project.chemistbe.dto;

import dthaibinhf.project.chemistbe.model.StudentDetailDTO;
import lombok.Value;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Group}
 */
@Value
public class GroupDTO implements Serializable {
    String name;
    String level;
    Integer feeId;
    String feeName;
    Integer academicYearId;
    String academicYear;
    Integer gradeId;
    String gradeName;
    Set<GroupScheduleDTO> groupSchedules;
    Set<ScheduleDTO> schedules;
    Set<StudentDetailDTO> studentDetails;
}