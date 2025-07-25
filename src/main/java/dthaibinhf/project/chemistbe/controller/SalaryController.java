package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.TeacherDTO;
import dthaibinhf.project.chemistbe.dto.TeacherMonthlySummaryDTO;
import dthaibinhf.project.chemistbe.model.SalaryType;
import dthaibinhf.project.chemistbe.service.SalaryCalculationService;
import dthaibinhf.project.chemistbe.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for managing teacher salary operations.
 * This controller provides endpoints for:
 * - Configuring teacher salary settings (salary type and base rate)
 * - Calculating monthly salaries for individual teachers or all teachers
 * - Retrieving salary history and summaries
 * - Managing salary calculations and recalculations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/salary")
@RequiredArgsConstructor
@Tag(name = "Salary Management", description = "Teacher salary calculation and management APIs")
public class SalaryController {

    private final SalaryCalculationService salaryCalculationService;
    private final TeacherService teacherService;

    // ===================== SALARY CONFIGURATION ENDPOINTS =====================

    /**
     * Update salary configuration for a specific teacher.
     * 
     * @param teacherId The ID of the teacher
     * @param salaryType The salary calculation type (PER_LESSON or FIXED)
     * @param baseRate The base rate for salary calculations
     * @return Updated teacher DTO with new salary configuration
     */
    @Operation(summary = "Update teacher salary configuration",
               description = "Configure salary type and base rate for a specific teacher")
    @ApiResponse(responseCode = "200", description = "Salary configuration updated successfully")
    @ApiResponse(responseCode = "404", description = "Teacher not found")
    @ApiResponse(responseCode = "400", description = "Invalid salary configuration parameters")
    @PutMapping("/teacher/{teacherId}/config")
    public ResponseEntity<TeacherDTO> updateTeacherSalaryConfig(
            @Parameter(description = "Teacher ID", example = "1")
            @PathVariable Integer teacherId,

            @Parameter(description = "Salary calculation type", example = "PER_LESSON")
            @RequestParam SalaryType salaryType,
            
            @Parameter(description = "Base rate for salary calculation", example = "500000.00")
            @RequestParam BigDecimal baseRate) {
        
        log.info("Updating salary config for teacher {}: type={}, rate={}", teacherId, salaryType, baseRate);
        
        TeacherDTO updatedTeacher = teacherService.updateSalaryConfiguration(teacherId, salaryType, baseRate);
        return ResponseEntity.ok(updatedTeacher);
    }

    /**
     * Get salary configuration for a specific teacher.
     * 
     * @param teacherId The ID of the teacher
     * @return Teacher DTO with current salary configuration
     */
    @Operation(summary = "Get teacher salary configuration",
               description = "Retrieve current salary configuration for a specific teacher")
    @ApiResponse(responseCode = "200", description = "Salary configuration retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Teacher not found")
    @GetMapping("/teacher/{teacherId}/config")
    public ResponseEntity<TeacherDTO> getTeacherSalaryConfig(
            @Parameter(description = "Teacher ID", example = "1")
            @PathVariable Integer teacherId) {
        
        TeacherDTO teacher = teacherService.getSalaryConfiguration(teacherId);
        return ResponseEntity.ok(teacher);
    }

    // ===================== SALARY CALCULATION ENDPOINTS =====================

    /**
     * Calculate monthly salary for a specific teacher.
     * 
     * @param teacherId The ID of the teacher
     * @param month The month (1-12)
     * @param year The year
     * @return Calculated monthly salary summary
     */
    @Operation(summary = "Calculate monthly salary for a teacher",
               description = "Calculate and save monthly salary summary for a specific teacher")
    @ApiResponse(responseCode = "201", description = "Monthly salary calculated successfully")
    @ApiResponse(responseCode = "404", description = "Teacher not found")
    @ApiResponse(responseCode = "400", description = "Invalid month/year or summary already exists")
    @PostMapping("/teacher/{teacherId}/calculate")
    public ResponseEntity<TeacherMonthlySummaryDTO> calculateTeacherMonthlySalary(
            @Parameter(description = "Teacher ID", example = "1")
            @PathVariable Integer teacherId,
            
            @Parameter(description = "Month (1-12)", example = "12")
            @RequestParam @Min(1) @Max(12) Integer month,
            
            @Parameter(description = "Year", example = "2024")
            @RequestParam @Min(2020) @Max(2100) Integer year) {
        
        log.info("Calculating monthly salary for teacher {} for {}/{}", teacherId, month, year);
        
        var summary = salaryCalculationService.calculateMonthlySalary(teacherId, month, year);
        var summaryDTO = teacherService.getTeacherMonthlySummary(teacherId, month, year);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(summaryDTO);
    }

    /**
     * Calculate monthly salaries for all active teachers.
     * 
     * @param month The month (1-12)
     * @param year The year
     * @return List of calculated monthly salary summaries
     */
    @Operation(summary = "Calculate monthly salaries for all teachers",
               description = "Calculate monthly salary summaries for all active teachers")
    @ApiResponse(responseCode = "200", description = "Monthly salaries calculated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid month/year")
    @PostMapping("/calculate-all")
    public ResponseEntity<List<TeacherMonthlySummaryDTO>> calculateAllTeachersSalaries(
            @Parameter(description = "Month (1-12)", example = "12")
            @RequestParam @Min(1) @Max(12) Integer month,
            
            @Parameter(description = "Year", example = "2024")
            @RequestParam @Min(2020) @Max(2100) Integer year) {
        
        log.info("Calculating monthly salaries for all teachers for {}/{}", month, year);
        
        var summaries = salaryCalculationService.calculateMonthlySalariesForAllTeachers(month, year);
        
        // Convert to DTOs
        List<TeacherMonthlySummaryDTO> summaryDTOs = summaries.stream()
                .map(summary -> teacherService.getTeacherMonthlySummary(
                        summary.getTeacher().getId(), summary.getMonth(), summary.getYear()))
                .toList();
        
        return ResponseEntity.ok(summaryDTOs);
    }

    /**
     * Recalculate existing monthly salary for a teacher.
     * 
     * @param teacherId The ID of the teacher
     * @param month The month (1-12)
     * @param year The year
     * @return Recalculated monthly salary summary
     */
    @Operation(summary = "Recalculate monthly salary for a teacher",
               description = "Recalculate existing monthly salary summary for a specific teacher")
    @ApiResponse(responseCode = "200", description = "Monthly salary recalculated successfully")
    @ApiResponse(responseCode = "404", description = "Teacher or salary summary not found")
    @ApiResponse(responseCode = "400", description = "Invalid month/year")
    @PutMapping("/teacher/{teacherId}/recalculate")
    public ResponseEntity<TeacherMonthlySummaryDTO> recalculateTeacherMonthlySalary(
            @Parameter(description = "Teacher ID", example = "1")
            @PathVariable Integer teacherId,
            
            @Parameter(description = "Month (1-12)", example = "12")
            @RequestParam @Min(1) @Max(12) Integer month,
            
            @Parameter(description = "Year", example = "2024")
            @RequestParam @Min(2020) @Max(2100) Integer year) {
        
        log.info("Recalculating monthly salary for teacher {} for {}/{}", teacherId, month, year);
        
        salaryCalculationService.recalculateMonthlySalary(teacherId, month, year);
        var summaryDTO = teacherService.getTeacherMonthlySummary(teacherId, month, year);
        
        return ResponseEntity.ok(summaryDTO);
    }

    // ===================== SALARY HISTORY AND REPORTING ENDPOINTS =====================

    /**
     * Get monthly salary summaries for a specific teacher.
     * 
     * @param teacherId The ID of the teacher
     * @param pageable Pagination information
     * @return Page of monthly salary summaries
     */
    @Operation(summary = "Get teacher salary summaries",
               description = "Retrieve paginated monthly salary summaries for a specific teacher")
    @ApiResponse(responseCode = "200", description = "Salary summaries retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Teacher not found")
    @GetMapping("/teacher/{teacherId}/summaries")
    public ResponseEntity<Page<TeacherMonthlySummaryDTO>> getTeacherSalarySummaries(
            @Parameter(description = "Teacher ID", example = "1")
            @PathVariable Integer teacherId,
            
            @PageableDefault(size = 12, sort = {"year", "month"}) Pageable pageable) {
        
        Page<TeacherMonthlySummaryDTO> summaries = teacherService.getTeacherSalarySummaries(teacherId, pageable);
        return ResponseEntity.ok(summaries);
    }

    /**
     * Get a specific monthly salary summary for a teacher.
     * 
     * @param teacherId The ID of the teacher
     * @param month The month (1-12)
     * @param year The year
     * @return Monthly salary summary
     */
    @Operation(summary = "Get specific monthly salary summary",
               description = "Retrieve a specific monthly salary summary for a teacher")
    @ApiResponse(responseCode = "200", description = "Salary summary retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Teacher or salary summary not found")
    @GetMapping("/teacher/{teacherId}/summary/{year}/{month}")
    public ResponseEntity<TeacherMonthlySummaryDTO> getTeacherMonthlySummary(
            @Parameter(description = "Teacher ID", example = "1")
            @PathVariable Integer teacherId,
            
            @Parameter(description = "Year", example = "2024")
            @PathVariable @Min(2020) @Max(2100) Integer year,
            
            @Parameter(description = "Month (1-12)", example = "12")
            @PathVariable @Min(1) @Max(12) Integer month) {
        
        TeacherMonthlySummaryDTO summary = teacherService.getTeacherMonthlySummary(teacherId, month, year);
        return ResponseEntity.ok(summary);
    }

    /**
     * Get salary history for a teacher within a date range.
     * 
     * @param teacherId The ID of the teacher
     * @param fromYear Starting year
     * @param fromMonth Starting month
     * @param toYear Ending year
     * @param toMonth Ending month
     * @return List of monthly summaries in the date range
     */
    @Operation(summary = "Get teacher salary history",
               description = "Retrieve salary history for a teacher within a specified date range")
    @ApiResponse(responseCode = "200", description = "Salary history retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Teacher not found")
    @ApiResponse(responseCode = "400", description = "Invalid date range")
    @GetMapping("/teacher/{teacherId}/history")
    public ResponseEntity<List<TeacherMonthlySummaryDTO>> getTeacherSalaryHistory(
            @Parameter(description = "Teacher ID", example = "1")
            @PathVariable Integer teacherId,
            
            @Parameter(description = "Starting year", example = "2024")
            @RequestParam @Min(2020) @Max(2100) Integer fromYear,
            
            @Parameter(description = "Starting month (1-12)", example = "1")
            @RequestParam @Min(1) @Max(12) Integer fromMonth,
            
            @Parameter(description = "Ending year", example = "2024")
            @RequestParam @Min(2020) @Max(2100) Integer toYear,
            
            @Parameter(description = "Ending month (1-12)", example = "12")
            @RequestParam @Min(1) @Max(12) Integer toMonth) {
        
        List<TeacherMonthlySummaryDTO> history = teacherService.getTeacherSalaryHistory(
                teacherId, fromYear, fromMonth, toYear, toMonth);
        
        return ResponseEntity.ok(history);
    }
}