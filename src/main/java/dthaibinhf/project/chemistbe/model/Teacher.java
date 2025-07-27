package dthaibinhf.project.chemistbe.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Model class representing a teacher entity in the system.
 * A teacher is associated with an account and can have multiple teacher details and schedules.
 */
@Getter
@Setter
@Entity
@Table(name = "teacher")
public class Teacher extends BaseEntity {
    /**
     * The account associated with this teacher.
     * Many teachers can be associated with one account.
     * Required field.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /**
     * The set of teacher details associated with this teacher.
     * One teacher can have many teacher details.
     */
    @OneToMany(mappedBy = "teacher")
    @JsonManagedReference
    private Set<TeacherDetail> teacherDetails = new LinkedHashSet<>();

    /**
     * The set of schedules associated with this teacher.
     * One teacher can have many schedules.
     * This field can be used to calculate the salary of the teacher.
     */
    @OneToMany(mappedBy = "teacher")
    @JsonManagedReference
    private Set<Schedule> schedules = new LinkedHashSet<>();

    /**
     * The type of salary calculation for this teacher.
     * Defaults to PER_LESSON calculation method.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "salary_type", length = 20)
    private SalaryType salaryType = SalaryType.PER_LESSON;

    /**
     * The base rate for salary calculations.
     * For PER_LESSON: rate per lesson
     * For FIXED: monthly base salary
     */
    @Column(name = "base_rate", precision = 10, scale = 2)
    private BigDecimal baseRate;

    /**
     * The set of monthly salary summaries for this teacher.
     * One teacher can have many monthly summaries for tracking salary history.
     */
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<TeacherMonthlySummary> monthlySummaries = new LinkedHashSet<>();
}
