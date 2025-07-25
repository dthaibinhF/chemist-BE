package dthaibinhf.project.chemistbe.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Model class representing a teacher's monthly salary summary.
 * This entity tracks detailed salary information and performance metrics for each teacher per month.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "teacher_monthly_summary",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_teacher_monthly_summary_unique", 
                           columnNames = {"teacher_id", "month", "year"})
       })
public class TeacherMonthlySummary extends BaseEntity {
    
    /**
     * The teacher associated with this monthly summary.
     * Many summaries belong to one teacher.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    @JsonBackReference
    private Teacher teacher;

    /**
     * The month for this salary summary (1-12).
     * Required field.
     */
    @Column(name = "month", nullable = false)
    private Integer month;

    /**
     * The year for this salary summary.
     * Required field.
     */
    @Column(name = "year", nullable = false)
    private Integer year;

    /**
     * Total number of lessons scheduled for the teacher in this month.
     * Default value is 0.
     */
    @Column(name = "scheduled_lessons", nullable = false)
    @Builder.Default
    private Integer scheduledLessons = 0;

    /**
     * Total number of lessons actually completed by the teacher in this month.
     * Default value is 0.
     */
    @Column(name = "completed_lessons", nullable = false)
    @Builder.Default
    private Integer completedLessons = 0;

    /**
     * Completion rate as a decimal (0.0000 to 1.0000).
     * Calculated as completed_lessons / scheduled_lessons.
     * Default value is 0.0000.
     */
    @Column(name = "completion_rate", nullable = false, precision = 5, scale = 4)
    @Builder.Default
    private BigDecimal completionRate = BigDecimal.ZERO;

    /**
     * Rate per lesson for salary calculation.
     * Required field with precision 10, scale 2.
     */
    @Column(name = "rate_per_lesson", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal ratePerLesson = BigDecimal.ZERO;

    /**
     * Base salary amount for the month.
     * For FIXED salary type, this is the fixed monthly amount.
     * For PER_LESSON type, this is calculated as completed_lessons * rate_per_lesson.
     */
    @Column(name = "base_salary", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal baseSalary = BigDecimal.ZERO;

    /**
     * Performance bonus amount based on completion rate or other metrics.
     * Optional field, defaults to 0.00.
     */
    @Column(name = "performance_bonus", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal performanceBonus = BigDecimal.ZERO;

    /**
     * Total salary for the month (base_salary + performance_bonus).
     * Required field with precision 10, scale 2.
     */
    @Column(name = "total_salary", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalSalary = BigDecimal.ZERO;
}