package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.FinancialStatisticsDTO;
import dthaibinhf.project.chemistbe.dto.PaymentDetailDTO;
import dthaibinhf.project.chemistbe.dto.StudentPaymentSummaryDTO;
import dthaibinhf.project.chemistbe.service.FinancialStatisticsService;
import dthaibinhf.project.chemistbe.service.PaymentOverdueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for financial reporting and overdue payment management.
 * 
 * This controller provides endpoints for:
 * - Financial dashboard and statistics
 * - Overdue payment detection and management
 * - Revenue reporting and analytics
 * - Collection rate analysis
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/financial")
@RequiredArgsConstructor
@Tag(name = "Financial Management", description = "Financial statistics and overdue payment management APIs")
public class FinancialController {

    private final FinancialStatisticsService financialStatisticsService;
    private final PaymentOverdueService paymentOverdueService;

    // ===================== FINANCIAL DASHBOARD ENDPOINTS =====================

    /**
     * Get comprehensive financial dashboard statistics.
     * 
     * @return Financial statistics including revenue, outstanding amounts, collection rates, etc.
     */
    @Operation(summary = "Get financial dashboard",
               description = "Retrieve comprehensive financial statistics for dashboard display")
    @ApiResponse(responseCode = "200", description = "Financial statistics retrieved successfully")
    @GetMapping("/dashboard")
    public ResponseEntity<FinancialStatisticsDTO> getFinancialDashboard() {
        
        log.info("Retrieving financial dashboard statistics");
        
        FinancialStatisticsDTO statistics = financialStatisticsService.getFinancialDashboard();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get financial statistics for a specific date range.
     * 
     * @param startDate Start date for the range (YYYY-MM-DD)
     * @param endDate End date for the range (YYYY-MM-DD)
     * @return Financial statistics for the specified date range
     */
    @Operation(summary = "Get financial statistics for date range",
               description = "Retrieve financial statistics for a specific date range")
    @ApiResponse(responseCode = "200", description = "Financial statistics retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid date range")
    @GetMapping("/statistics")
    public ResponseEntity<FinancialStatisticsDTO> getFinancialStatisticsForDateRange(
            @Parameter(description = "Start date (YYYY-MM-DD)", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date (YYYY-MM-DD)", example = "2024-12-31") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("Retrieving financial statistics for date range: {} to {}", startDate, endDate);
        
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }
        
        FinancialStatisticsDTO statistics = financialStatisticsService.getFinancialStatisticsForDateRange(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    // ===================== OVERDUE PAYMENT MANAGEMENT ENDPOINTS =====================

    /**
     * Get all overdue payment details.
     * 
     * @return List of overdue payment detail DTOs
     */
    @Operation(summary = "Get overdue payment details",
               description = "Retrieve all payment details that are past their due date")
    @ApiResponse(responseCode = "200", description = "Overdue payment details retrieved successfully")
    @GetMapping("/overdue/details")
    public ResponseEntity<List<PaymentDetailDTO>> getOverduePaymentDetails() {
        
        log.info("Retrieving overdue payment details");
        
        List<PaymentDetailDTO> overdueDetails = paymentOverdueService.getOverduePaymentDetails();
        return ResponseEntity.ok(overdueDetails);
    }

    /**
     * Get all overdue payment summaries.
     * 
     * @return List of overdue payment summary DTOs
     */
    @Operation(summary = "Get overdue payment summaries",
               description = "Retrieve all payment summaries that are past their due date")
    @ApiResponse(responseCode = "200", description = "Overdue payment summaries retrieved successfully")
    @GetMapping("/overdue/summaries")
    public ResponseEntity<List<StudentPaymentSummaryDTO>> getOverduePaymentSummaries() {
        
        log.info("Retrieving overdue payment summaries");
        
        List<StudentPaymentSummaryDTO> overdueSummaries = paymentOverdueService.getOverduePaymentSummaries();
        return ResponseEntity.ok(overdueSummaries);
    }

    /**
     * Get overdue payments for a specific student.
     * 
     * @param studentId The student ID
     * @return List of overdue payment summaries for the student
     */
    @Operation(summary = "Get student overdue payments",
               description = "Retrieve overdue payments for a specific student")
    @ApiResponse(responseCode = "200", description = "Student overdue payments retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Student not found")
    @GetMapping("/overdue/student/{studentId}")
    public ResponseEntity<List<StudentPaymentSummaryDTO>> getOverduePaymentsForStudent(
            @Parameter(description = "Student ID", example = "1")
            @PathVariable Integer studentId) {
        
        log.info("Retrieving overdue payments for student {}", studentId);
        
        List<StudentPaymentSummaryDTO> overduePayments = paymentOverdueService.getOverduePaymentsForStudent(studentId);
        return ResponseEntity.ok(overduePayments);
    }

    /**
     * Update payment statuses for all overdue payments.
     * This endpoint runs a batch process to mark overdue payments.
     * 
     * @return Number of payments updated to overdue status
     */
    @Operation(summary = "Update overdue payment statuses",
               description = "Batch update payment statuses to mark overdue payments")
    @ApiResponse(responseCode = "200", description = "Overdue payment statuses updated successfully")
    @PostMapping("/overdue/update-statuses")
    public ResponseEntity<Integer> updateOverduePaymentStatuses() {
        
        log.info("Starting batch update of overdue payment statuses");
        
        int updatedCount = paymentOverdueService.updateOverduePaymentStatuses();
        return ResponseEntity.ok(updatedCount);
    }

    /**
     * Get overdue payment statistics.
     * 
     * @return Overdue payment statistics
     */
    @Operation(summary = "Get overdue payment statistics",
               description = "Retrieve statistics about overdue payments")
    @ApiResponse(responseCode = "200", description = "Overdue payment statistics retrieved successfully")
    @GetMapping("/overdue/statistics")
    public ResponseEntity<PaymentOverdueService.OverdueStatistics> getOverdueStatistics() {
        
        log.info("Retrieving overdue payment statistics");
        
        PaymentOverdueService.OverdueStatistics statistics = paymentOverdueService.getOverdueStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Check if a specific student has any overdue payments.
     * 
     * @param studentId The student ID
     * @return Boolean indicating if the student has overdue payments
     */
    @Operation(summary = "Check if student has overdue payments",
               description = "Check if a specific student has any overdue payments")
    @ApiResponse(responseCode = "200", description = "Overdue payment check completed successfully")
    @ApiResponse(responseCode = "404", description = "Student not found")
    @GetMapping("/overdue/student/{studentId}/check")
    public ResponseEntity<Boolean> hasOverduePayments(
            @Parameter(description = "Student ID", example = "1")
            @PathVariable Integer studentId) {
        
        log.info("Checking if student {} has overdue payments", studentId);
        
        boolean hasOverdue = paymentOverdueService.hasOverduePayments(studentId);
        return ResponseEntity.ok(hasOverdue);
    }

    /**
     * Get the number of days a payment summary is overdue.
     * 
     * @param summaryId The payment summary ID
     * @return Number of days overdue (negative if not overdue)
     */
    @Operation(summary = "Get days overdue for payment summary",
               description = "Get the number of days a payment summary is overdue")
    @ApiResponse(responseCode = "200", description = "Days overdue calculated successfully")
    @ApiResponse(responseCode = "404", description = "Payment summary not found")
    @GetMapping("/overdue/summary/{summaryId}/days")
    public ResponseEntity<Long> getDaysOverdue(
            @Parameter(description = "Payment summary ID", example = "1")
            @PathVariable Integer summaryId) {
        
        log.info("Calculating days overdue for payment summary {}", summaryId);
        
        long daysOverdue = paymentOverdueService.getDaysOverdue(summaryId);
        return ResponseEntity.ok(daysOverdue);
    }
}