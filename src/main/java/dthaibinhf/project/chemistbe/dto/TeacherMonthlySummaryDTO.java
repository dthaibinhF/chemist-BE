package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) for {@link dthaibinhf.project.chemistbe.model.TeacherMonthlySummary}
 * 
 * This class is used for transferring teacher monthly salary summary data between layers.
 * It contains all necessary fields to represent a teacher's salary information for a specific month.
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class TeacherMonthlySummaryDTO extends BaseDTO implements Serializable {
    
    /**
     * The ID of the teacher associated with this summary.
     * Required field, mapped to "teacher_id" in JSON.
     */
    @NotNull
    @JsonProperty("teacher_id")
    Integer teacherId;

    /**
     * The name of the teacher for display purposes.
     * Mapped to "teacher_name" in JSON.
     */
    @JsonProperty("teacher_name")
    String teacherName;

    /**
     * The month for this salary summary (1-12).
     * Required field.
     */
    @NotNull
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    Integer month;

    /**
     * The year for this salary summary.
     * Required field.
     */
    @NotNull
    @Min(value = 2020, message = "Year must be 2020 or later")
    @Max(value = 2100, message = "Year must be 2100 or earlier")
    Integer year;

    /**
     * Total number of lessons scheduled for the teacher in this month.
     * Mapped to "scheduled_lessons" in JSON.
     */
    @NotNull
    @PositiveOrZero
    @JsonProperty("scheduled_lessons")
    Integer scheduledLessons;

    /**
     * Total number of lessons actually completed by the teacher in this month.
     * Mapped to "completed_lessons" in JSON.
     */
    @NotNull
    @PositiveOrZero
    @JsonProperty("completed_lessons")
    Integer completedLessons;

    /**
     * Completion rate as a decimal (0.0000 to 1.0000).
     * Mapped to "completion_rate" in JSON.
     */
    @NotNull
    @DecimalMin(value = "0.0000", message = "Completion rate must be between 0 and 1")
    @DecimalMax(value = "1.0000", message = "Completion rate must be between 0 and 1")
    @JsonProperty("completion_rate")
    BigDecimal completionRate;

    /**
     * Rate per lesson for salary calculation.
     * Mapped to "rate_per_lesson" in JSON.
     */
    @NotNull
    @PositiveOrZero
    @JsonProperty("rate_per_lesson")
    BigDecimal ratePerLesson;

    /**
     * Base salary amount for the month.
     * Mapped to "base_salary" in JSON.
     */
    @NotNull
    @PositiveOrZero
    @JsonProperty("base_salary")
    BigDecimal baseSalary;

    /**
     * Performance bonus amount.
     * Mapped to "performance_bonus" in JSON.
     */
    @PositiveOrZero
    @JsonProperty("performance_bonus")
    BigDecimal performanceBonus;

    /**
     * Total salary for the month.
     * Mapped to "total_salary" in JSON.
     */
    @NotNull
    @PositiveOrZero
    @JsonProperty("total_salary")
    BigDecimal totalSalary;
}