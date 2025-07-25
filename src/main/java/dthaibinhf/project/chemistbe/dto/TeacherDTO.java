package dthaibinhf.project.chemistbe.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import dthaibinhf.project.chemistbe.model.SalaryType;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * DTO (Data Transfer Object) for {@link dthaibinhf.project.chemistbe.model.Teacher}
 * 
 * This class is used for transferring teacher data between layers, particularly
 * between the service layer and the controller layer. It contains all the necessary
 * fields to represent a teacher in the system.
 */
@Value
@EqualsAndHashCode(callSuper = false)
public class TeacherDTO extends BaseDTO implements Serializable {
    /**
     * The account associated with this teacher.
     */
    AccountDTO account;

    /**
     * The set of teacher details associated with this teacher.
     */
    Set<TeacherDetailDTO> teacherDetails;

    /**
     * The set of schedules associated with this teacher.
     * This can be used to calculate the salary of the teacher.
     */
    Set<ScheduleDTO> schedules;

    /**
     * The type of salary calculation for this teacher.
     * Mapped to "salary_type" in JSON.
     */
    @JsonProperty("salary_type")
    SalaryType salaryType;

    /**
     * The base rate for salary calculations.
     * For PER_LESSON: rate per lesson
     * For FIXED: monthly base salary
     * Mapped to "base_rate" in JSON.
     */
    @JsonProperty("base_rate")
    BigDecimal baseRate;
}
