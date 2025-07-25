package dthaibinhf.project.chemistbe.repository;

import dthaibinhf.project.chemistbe.model.TeacherMonthlySummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TeacherMonthlySummary entity.
 * This repository provides database access methods for teacher monthly salary summaries
 * with support for custom queries and filtering.
 */
@Repository
public interface TeacherMonthlySummaryRepository extends JpaRepository<TeacherMonthlySummary, Integer> {
    
    /**
     * Find a specific monthly summary for a teacher by month and year.
     * Only returns non-deleted records (soft delete support).
     * 
     * @param teacherId The ID of the teacher
     * @param month The month (1-12)
     * @param year The year
     * @return Optional containing the summary if found
     */
    @Query("SELECT tms FROM TeacherMonthlySummary tms WHERE tms.teacher.id = :teacherId " +
           "AND tms.month = :month AND tms.year = :year AND tms.endAt IS NULL")
    Optional<TeacherMonthlySummary> findByTeacherIdAndMonthAndYear(
            @Param("teacherId") Integer teacherId, 
            @Param("month") Integer month, 
            @Param("year") Integer year);
    
    /**
     * Find all monthly summaries for a specific teacher.
     * Only returns non-deleted records, ordered by year and month descending.
     * 
     * @param teacherId The ID of the teacher
     * @param pageable Pagination information
     * @return Page of monthly summaries
     */
    @Query("SELECT tms FROM TeacherMonthlySummary tms WHERE tms.teacher.id = :teacherId " +
           "AND tms.endAt IS NULL ORDER BY tms.year DESC, tms.month DESC")
    Page<TeacherMonthlySummary> findByTeacherIdOrderByYearDescMonthDesc(
            @Param("teacherId") Integer teacherId, 
            Pageable pageable);
    
    /**
     * Find all monthly summaries for a specific month and year across all teachers.
     * Only returns non-deleted records.
     * 
     * @param month The month (1-12)
     * @param year The year
     * @return List of monthly summaries for all teachers
     */
    @Query("SELECT tms FROM TeacherMonthlySummary tms WHERE tms.month = :month " +
           "AND tms.year = :year AND tms.endAt IS NULL ORDER BY tms.teacher.account.name")
    List<TeacherMonthlySummary> findByMonthAndYear(
            @Param("month") Integer month, 
            @Param("year") Integer year);
    
    /**
     * Find all monthly summaries for a specific year across all teachers.
     * Only returns non-deleted records.
     * 
     * @param year The year
     * @param pageable Pagination information
     * @return Page of monthly summaries for the year
     */
    @Query("SELECT tms FROM TeacherMonthlySummary tms WHERE tms.year = :year " +
           "AND tms.endAt IS NULL ORDER BY tms.month DESC, tms.teacher.account.name")
    Page<TeacherMonthlySummary> findByYear(@Param("year") Integer year, Pageable pageable);
    
    /**
     * Check if a monthly summary already exists for a teacher in a specific month/year.
     * Used to prevent duplicate salary calculations.
     * 
     * @param teacherId The ID of the teacher
     * @param month The month (1-12)
     * @param year The year
     * @return True if a summary exists, false otherwise
     */
    @Query("SELECT COUNT(tms) > 0 FROM TeacherMonthlySummary tms WHERE tms.teacher.id = :teacherId " +
           "AND tms.month = :month AND tms.year = :year AND tms.endAt IS NULL")
    boolean existsByTeacherIdAndMonthAndYear(
            @Param("teacherId") Integer teacherId, 
            @Param("month") Integer month, 
            @Param("year") Integer year);
    
    /**
     * Get salary statistics for a specific teacher across multiple months.
     * 
     * @param teacherId The ID of the teacher
     * @param fromYear Starting year for the range
     * @param fromMonth Starting month for the range
     * @param toYear Ending year for the range
     * @param toMonth Ending month for the range
     * @return List of monthly summaries in the date range
     */
    @Query("SELECT tms FROM TeacherMonthlySummary tms WHERE tms.teacher.id = :teacherId " +
           "AND tms.endAt IS NULL " +
           "AND ((tms.year = :fromYear AND tms.month >= :fromMonth) OR tms.year > :fromYear) " +
           "AND ((tms.year = :toYear AND tms.month <= :toMonth) OR tms.year < :toYear) " +
           "ORDER BY tms.year, tms.month")
    List<TeacherMonthlySummary> findTeacherSalaryInDateRange(
            @Param("teacherId") Integer teacherId,
            @Param("fromYear") Integer fromYear,
            @Param("fromMonth") Integer fromMonth,
            @Param("toYear") Integer toYear,
            @Param("toMonth") Integer toMonth);
    
    /**
     * Get top performing teachers by completion rate for a specific month/year.
     * 
     * @param month The month (1-12)
     * @param year The year
     * @param pageable Pagination information
     * @return Page of monthly summaries ordered by completion rate descending
     */
    @Query("SELECT tms FROM TeacherMonthlySummary tms WHERE tms.month = :month " +
           "AND tms.year = :year AND tms.endAt IS NULL " +
           "ORDER BY tms.completionRate DESC, tms.totalSalary DESC")
    Page<TeacherMonthlySummary> findTopPerformingTeachers(
            @Param("month") Integer month, 
            @Param("year") Integer year, 
            Pageable pageable);
    
    /**
     * Get all active monthly summaries (non-deleted) with pagination.
     * 
     * @param pageable Pagination information
     * @return Page of all active monthly summaries
     */
    @Query("SELECT tms FROM TeacherMonthlySummary tms WHERE tms.endAt IS NULL " +
           "ORDER BY tms.year DESC, tms.month DESC, tms.teacher.account.name")
    Page<TeacherMonthlySummary> findAllActive(Pageable pageable);
}