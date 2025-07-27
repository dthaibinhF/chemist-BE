package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.TeacherDTO;
import dthaibinhf.project.chemistbe.dto.TeacherMonthlySummaryDTO;
import dthaibinhf.project.chemistbe.model.SalaryType;
import dthaibinhf.project.chemistbe.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Tag(name = "Teacher Management", description = "Teacher CRUD operations and salary configuration")
public class TeacherController {

    TeacherService teacherService;

    @PostMapping
    public ResponseEntity<TeacherDTO> createTeacher(@Valid @RequestBody TeacherDTO teacherDTO) {
        return ResponseEntity.ok(teacherService.createTeacher(teacherDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherDTO> getTeacher(@PathVariable Integer id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @GetMapping
    public ResponseEntity<List<TeacherDTO>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeacherDTO> updateTeacher(@PathVariable Integer id, @Valid @RequestBody TeacherDTO teacherDTO) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, teacherDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeacher(@PathVariable Integer id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TeacherDTO>> searchTeachers(
            @PageableDefault Pageable pageable,
            @RequestParam(value = "teacherName", required = false) String teacherName,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "email", required = false) String email
    ) {
        return ResponseEntity.ok(teacherService.search(pageable, teacherName, phone, email));
    }

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
    @PutMapping("/{teacherId}/salary-config")
    public ResponseEntity<TeacherDTO> updateSalaryConfiguration(
            @Parameter(description = "Teacher ID", example = "1")
            @PathVariable Integer teacherId,
            
            @Parameter(description = "Salary calculation type", example = "PER_LESSON")
            @RequestParam SalaryType salaryType,
            
            @Parameter(description = "Base rate for salary calculation", example = "500000.00")
            @RequestParam BigDecimal baseRate) {
        
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
    @GetMapping("/{teacherId}/salary-config")
    public ResponseEntity<TeacherDTO> getSalaryConfiguration(
            @Parameter(description = "Teacher ID", example = "1")
            @PathVariable Integer teacherId) {
        
        TeacherDTO teacher = teacherService.getSalaryConfiguration(teacherId);
        return ResponseEntity.ok(teacher);
    }

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
    @GetMapping("/{teacherId}/salary-summaries")
    public ResponseEntity<Page<TeacherMonthlySummaryDTO>> getSalarySummaries(
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
    @GetMapping("/{teacherId}/salary-summary/{year}/{month}")
    public ResponseEntity<TeacherMonthlySummaryDTO> getMonthlySalarySummary(
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
    @GetMapping("/{teacherId}/salary-history")
    public ResponseEntity<List<TeacherMonthlySummaryDTO>> getSalaryHistory(
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