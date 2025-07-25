package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.model.*;
import dthaibinhf.project.chemistbe.repository.ScheduleRepository;
import dthaibinhf.project.chemistbe.repository.TeacherMonthlySummaryRepository;
import dthaibinhf.project.chemistbe.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Service for calculating teacher salaries based on their schedules and attendance.
 * 
 * This service provides core business logic for:
 * - Calculating monthly salaries for individual teachers
 * - Processing bulk salary calculations for all teachers
 * - Determining performance bonuses based on completion rates
 * - Managing different salary calculation types (PER_LESSON vs FIXED)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SalaryCalculationService {
    
    private final TeacherRepository teacherRepository;
    private final ScheduleRepository scheduleRepository;
    private final TeacherMonthlySummaryRepository monthlySummaryRepository;
    
    // Performance bonus thresholds
    private static final BigDecimal EXCELLENT_PERFORMANCE_THRESHOLD = new BigDecimal("0.95"); // 95%
    private static final BigDecimal GOOD_PERFORMANCE_THRESHOLD = new BigDecimal("0.85"); // 85%
    private static final BigDecimal EXCELLENT_BONUS_RATE = new BigDecimal("0.15"); // 15% bonus
    private static final BigDecimal GOOD_BONUS_RATE = new BigDecimal("0.10"); // 10% bonus
    
    /**
     * Calculate and save monthly salary summary for a specific teacher.
     * 
     * @param teacherId The ID of the teacher
     * @param month The month (1-12)
     * @param year The year
     * @return The calculated monthly summary
     * @throws IllegalArgumentException if teacher not found or invalid date
     * @throws IllegalStateException if summary already exists for the month
     */
    public TeacherMonthlySummary calculateMonthlySalary(Integer teacherId, Integer month, Integer year) {
        log.info("Calculating monthly salary for teacher {} for {}/{}", teacherId, month, year);
        
        // Validate inputs
        validateDateInputs(month, year);
        
        // Check if teacher exists
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + teacherId));
        
        // Check if summary already exists
        if (monthlySummaryRepository.existsByTeacherIdAndMonthAndYear(teacherId, month, year)) {
            throw new IllegalStateException("Monthly salary summary already exists for teacher " + 
                                          teacherId + " for " + month + "/" + year);
        }
        
        // Calculate lesson metrics
        LessonMetrics metrics = calculateLessonMetrics(teacherId, month, year);
        
        // Calculate salary based on teacher's salary type
        SalaryCalculation calculation = calculateSalaryAmount(teacher, metrics);
        
        // Create and save monthly summary
        TeacherMonthlySummary summary = TeacherMonthlySummary.builder()
                .teacher(teacher)
                .month(month)
                .year(year)
                .scheduledLessons(metrics.scheduledLessons)
                .completedLessons(metrics.completedLessons)
                .completionRate(metrics.completionRate)
                .ratePerLesson(calculation.ratePerLesson)
                .baseSalary(calculation.baseSalary)
                .performanceBonus(calculation.performanceBonus)
                .totalSalary(calculation.totalSalary)
                .build();
        
        TeacherMonthlySummary savedSummary = monthlySummaryRepository.save(summary);
        
        log.info("Successfully calculated salary for teacher {}: Base={}, Bonus={}, Total={}", 
                teacherId, calculation.baseSalary, calculation.performanceBonus, calculation.totalSalary);
        
        return savedSummary;
    }
    
    /**
     * Calculate monthly salaries for all active teachers.
     * 
     * @param month The month (1-12)
     * @param year The year
     * @return List of calculated monthly summaries
     */
    public List<TeacherMonthlySummary> calculateMonthlySalariesForAllTeachers(Integer month, Integer year) {
        log.info("Calculating monthly salaries for all teachers for {}/{}", month, year);
        
        validateDateInputs(month, year);
        
        List<Teacher> activeTeachers = teacherRepository.findAllActiveTeachers();
        
        return activeTeachers.stream()
                .filter(teacher -> !monthlySummaryRepository.existsByTeacherIdAndMonthAndYear(teacher.getId(), month, year))
                .map(teacher -> {
                    try {
                        return calculateMonthlySalary(teacher.getId(), month, year);
                    } catch (Exception e) {
                        log.error("Failed to calculate salary for teacher {}: {}", teacher.getId(), e.getMessage());
                        return null;
                    }
                })
                .filter(summary -> summary != null)
                .toList();
    }
    
    /**
     * Recalculate existing monthly salary summary.
     * 
     * @param teacherId The ID of the teacher
     * @param month The month (1-12)
     * @param year The year
     * @return The recalculated monthly summary
     */
    public TeacherMonthlySummary recalculateMonthlySalary(Integer teacherId, Integer month, Integer year) {
        log.info("Recalculating monthly salary for teacher {} for {}/{}", teacherId, month, year);
        
        // Find existing summary
        TeacherMonthlySummary existingSummary = monthlySummaryRepository
                .findByTeacherIdAndMonthAndYear(teacherId, month, year)
                .orElseThrow(() -> new IllegalArgumentException("No salary summary found for teacher " + 
                                                              teacherId + " for " + month + "/" + year));
        
        Teacher teacher = existingSummary.getTeacher();
        
        // Recalculate metrics and salary
        LessonMetrics metrics = calculateLessonMetrics(teacherId, month, year);
        SalaryCalculation calculation = calculateSalaryAmount(teacher, metrics);
        
        // Update existing summary
        existingSummary.setScheduledLessons(metrics.scheduledLessons);
        existingSummary.setCompletedLessons(metrics.completedLessons);
        existingSummary.setCompletionRate(metrics.completionRate);
        existingSummary.setRatePerLesson(calculation.ratePerLesson);
        existingSummary.setBaseSalary(calculation.baseSalary);
        existingSummary.setPerformanceBonus(calculation.performanceBonus);
        existingSummary.setTotalSalary(calculation.totalSalary);
        
        return monthlySummaryRepository.save(existingSummary);
    }
    
    /**
     * Calculate lesson metrics for a teacher in a specific month/year.
     */
    private LessonMetrics calculateLessonMetrics(Integer teacherId, Integer month, Integer year) {
        // Get date range for the month
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        OffsetDateTime startDateTime = startDate.atStartOfDay(ZoneId.of("Asia/Ho_Chi_Minh")).toOffsetDateTime();
        OffsetDateTime endDateTime = endDate.atTime(23, 59, 59).atZone(ZoneId.of("Asia/Ho_Chi_Minh")).toOffsetDateTime();
        
        // Get all schedules for the teacher in the date range
        List<Schedule> schedules = scheduleRepository.findByTeacherIdAndDateRange(teacherId, startDateTime, endDateTime);
        
        int scheduledLessons = schedules.size();
        
        // Count completed lessons (schedules with at least one attendance marked as completed)
        long completedLessons = schedules.stream()
                .mapToLong(schedule -> schedule.getAttendances().stream()
                        .mapToLong(attendance -> "PRESENT".equalsIgnoreCase(attendance.getStatus()) ? 1L : 0L)
                        .sum())
                .sum();
        
        // Calculate completion rate
        BigDecimal completionRate = scheduledLessons > 0 
                ? BigDecimal.valueOf(completedLessons).divide(BigDecimal.valueOf(scheduledLessons), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        return new LessonMetrics((int) completedLessons, scheduledLessons, completionRate);
    }
    
    /**
     * Calculate salary amount based on teacher's salary type and lesson metrics.
     */
    private SalaryCalculation calculateSalaryAmount(Teacher teacher, LessonMetrics metrics) {
        BigDecimal ratePerLesson = teacher.getBaseRate() != null ? teacher.getBaseRate() : BigDecimal.ZERO;
        BigDecimal baseSalary;
        
        if (teacher.getSalaryType() == SalaryType.FIXED) {
            // Fixed salary regardless of lesson count
            baseSalary = ratePerLesson;
        } else {
            // Per-lesson calculation
            baseSalary = ratePerLesson.multiply(BigDecimal.valueOf(metrics.completedLessons));
        }
        
        // Calculate performance bonus
        BigDecimal performanceBonus = calculatePerformanceBonus(baseSalary, metrics.completionRate);
        
        // Total salary
        BigDecimal totalSalary = baseSalary.add(performanceBonus);
        
        return new SalaryCalculation(ratePerLesson, baseSalary, performanceBonus, totalSalary);
    }
    
    /**
     * Calculate performance bonus based on completion rate.
     */
    private BigDecimal calculatePerformanceBonus(BigDecimal baseSalary, BigDecimal completionRate) {
        if (completionRate.compareTo(EXCELLENT_PERFORMANCE_THRESHOLD) >= 0) {
            return baseSalary.multiply(EXCELLENT_BONUS_RATE);
        } else if (completionRate.compareTo(GOOD_PERFORMANCE_THRESHOLD) >= 0) {
            return baseSalary.multiply(GOOD_BONUS_RATE);
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Validate month and year inputs.
     */
    private void validateDateInputs(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        }
        if (year == null || year < 2020 || year > 2100) {
            throw new IllegalArgumentException("Year must be between 2020 and 2100");
        }
        
        // Prevent calculation for future months
        LocalDate now = LocalDate.now();
        LocalDate requestedDate = LocalDate.of(year, month, 1);
        if (requestedDate.isAfter(now.withDayOfMonth(1))) {
            throw new IllegalArgumentException("Cannot calculate salary for future months");
        }
    }
    
    /**
     * Inner class to hold lesson metrics.
     */
    private record LessonMetrics(int completedLessons, int scheduledLessons, BigDecimal completionRate) {}
    
    /**
     * Inner class to hold salary calculation results.
     */
    private record SalaryCalculation(BigDecimal ratePerLesson, BigDecimal baseSalary, 
                                   BigDecimal performanceBonus, BigDecimal totalSalary) {}
}