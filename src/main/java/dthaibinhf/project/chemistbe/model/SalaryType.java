package dthaibinhf.project.chemistbe.model;

/**
 * Enumeration for different types of salary calculation methods for teachers.
 * This enum defines how a teacher's salary is calculated:
 * - PER_LESSON: Salary is calculated based on the number of lessons taught
 * - FIXED: Teacher rseceives a fixed monthly salary regardless of lesson count
 */
public enum SalaryType {
    /**
     * Salary calculated per lesson taught.
     * Formula: total_salary = completed_lessons * rate_per_lesson + performance_bonus
     */
    PER_LESSON,
    
    /**
     * Fixed monthly salary with potential bonuses.
     * Formula: total_salary = base_salary + performance_bonus
     */
    FIXED
}