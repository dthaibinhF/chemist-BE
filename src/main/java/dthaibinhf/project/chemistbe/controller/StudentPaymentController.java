package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.StudentPaymentSummaryDTO;
import dthaibinhf.project.chemistbe.service.StudentPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing student payment operations.
 * 
 * This controller provides endpoints for:
 * - Generating payment obligations for students and groups
 * - Retrieving payment summaries and status
 * - Managing payment obligations lifecycle
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/student-payment")
@RequiredArgsConstructor
@Tag(name = "Student Payment Management", description = "Student payment obligation and tracking APIs")
public class StudentPaymentController {

    private final StudentPaymentService studentPaymentService;

    // ===================== PAYMENT GENERATION ENDPOINTS =====================

    /**
     * Generate payment obligation for a student joining a group.
     * 
     * @param studentId The ID of the student
     * @param groupId The ID of the group
     * @return Created payment summary
     */
    @Operation(summary = "Generate payment for student in group",
               description = "Create payment obligation when a student joins a group")
    @ApiResponse(responseCode = "201", description = "Payment obligation created successfully")
    @ApiResponse(responseCode = "404", description = "Student or group not found")
    @ApiResponse(responseCode = "400", description = "Payment obligation already exists")
    @PostMapping("/student/{studentId}/group/{groupId}")
    public ResponseEntity<StudentPaymentSummaryDTO> generatePaymentForStudentInGroup(
            @Parameter(description = "Student ID", example = "1")
            @PathVariable Integer studentId,
            
            @Parameter(description = "Group ID", example = "1")
            @PathVariable Integer groupId) {
        
        log.info("Generating payment for student {} in group {}", studentId, groupId);
        
        StudentPaymentSummaryDTO summary = studentPaymentService.generatePaymentForStudentInGroup(studentId, groupId);
        return ResponseEntity.status(HttpStatus.CREATED).body(summary);
    }

    /**
     * Generate payment obligations for all students in a group.
     * 
     * @param groupId The ID of the group
     * @return List of created payment summaries
     */
    @Operation(summary = "Generate payments for entire group",
               description = "Create payment obligations for all students in a group")
    @ApiResponse(responseCode = "200", description = "Payment obligations created successfully")
    @ApiResponse(responseCode = "404", description = "Group not found")
    @PostMapping("/group/{groupId}/generate-all")
    public ResponseEntity<List<StudentPaymentSummaryDTO>> generatePaymentsForGroup(
            @Parameter(description = "Group ID", example = "1")
            @PathVariable Integer groupId) {
        
        log.info("Generating payments for all students in group {}", groupId);
        
        List<StudentPaymentSummaryDTO> summaries = studentPaymentService.generatePaymentsForGroup(groupId);
        return ResponseEntity.ok(summaries);
    }

    // ===================== PAYMENT SUMMARY RETRIEVAL ENDPOINTS =====================

    //get student payment summaries with fee id
    /**
     * Get payment summaries for a specific fee and student.
     *
     * @param feeId The fee ID
     * @param studentId The student ID
     * @return payment summaries for the fee and student
     */
    @Operation(summary = "Get payment summaries by fee and student",
               description = "Retrieve payment obligations for a specific fee and student")
    @ApiResponse(responseCode = "200", description = "Payment summaries retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Fee or student not found")
    @GetMapping("/student/{studentId}/fee/{feeId}")
    public ResponseEntity<StudentPaymentSummaryDTO> getPaymentSummariesByFeeAndStudent(
            @Parameter(description = "Fee ID", example = "1")
            @PathVariable Integer feeId,
            @Parameter(description = "Student ID", example = "1")
            @PathVariable Integer studentId) {
        log.info("Retrieving payment summaries for fee {} and student {}", feeId, studentId);
        StudentPaymentSummaryDTO summaries = studentPaymentService.getPaymentSummariesByFeeAndStudent(feeId, studentId);
        return ResponseEntity.ok(summaries);
    }

    /**
     * Get all payment summaries for a specific student.
     * 
     * @param studentId The student ID
     * @return List of payment summaries for the student
     */
    @Operation(summary = "Get student payment summaries",
               description = "Retrieve all payment obligations for a specific student")
    @ApiResponse(responseCode = "200", description = "Payment summaries retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Student not found")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentPaymentSummaryDTO>> getStudentPaymentSummaries(
            @Parameter(description = "Student ID", example = "1")
            @PathVariable Integer studentId) {
        
        List<StudentPaymentSummaryDTO> summaries = studentPaymentService.getStudentPaymentSummaries(studentId);
        return ResponseEntity.ok(summaries);
    }

    /**
     * Get all payment summaries for a specific group.
     * 
     * @param groupId The group ID
     * @return List of payment summaries for the group
     */
    @Operation(summary = "Get group payment summaries",
               description = "Retrieve all payment obligations for students in a group")
    @ApiResponse(responseCode = "200", description = "Payment summaries retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Group not found")
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<StudentPaymentSummaryDTO>> getGroupPaymentSummaries(
            @Parameter(description = "Group ID", example = "1")
            @PathVariable Integer groupId) {
        
        List<StudentPaymentSummaryDTO> summaries = studentPaymentService.getGroupPaymentSummaries(groupId);
        return ResponseEntity.ok(summaries);
    }

    /**
     * Get a specific payment summary by ID.
     * 
     * @param summaryId The payment summary ID
     * @return Payment summary details
     */
    @Operation(summary = "Get payment summary by ID",
               description = "Retrieve details of a specific payment summary")
    @ApiResponse(responseCode = "200", description = "Payment summary retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Payment summary not found")
    @GetMapping("/summary/{summaryId}")
    public ResponseEntity<StudentPaymentSummaryDTO> getPaymentSummaryById(
            @Parameter(description = "Payment summary ID", example = "1")
            @PathVariable Integer summaryId) {
        
        StudentPaymentSummaryDTO summary = studentPaymentService.getPaymentSummaryById(summaryId);
        return ResponseEntity.ok(summary);
    }

    // ===================== PAYMENT MANAGEMENT ENDPOINTS =====================

    /**
     * Update payment summary after a payment is made.
     * This endpoint should be called after a payment detail is created or updated.
     * 
     * @param studentId The student ID
     * @param feeId The fee ID
     * @param academicYearId The academic year ID
     * @param groupId The group ID (optional)
     * @return Success response
     */
    @Operation(summary = "Update payment summary after payment",
               description = "Recalculate payment summary totals and status after a payment is made")
    @ApiResponse(responseCode = "200", description = "Payment summary updated successfully")
    @ApiResponse(responseCode = "404", description = "Payment summary not found")
    @PutMapping("/update-after-payment")
    public ResponseEntity<Void> updatePaymentSummaryAfterPayment(
            @Parameter(description = "Student ID", example = "1")
            @RequestParam Integer studentId,
            
            @Parameter(description = "Fee ID", example = "1")
            @RequestParam Integer feeId,
            
            @Parameter(description = "Academic Year ID", example = "1")
            @RequestParam Integer academicYearId,
            
            @Parameter(description = "Group ID (optional)", example = "1")
            @RequestParam(required = false) Integer groupId) {
        
        log.info("Updating payment summary for student {} after payment", studentId);
        
        studentPaymentService.updatePaymentSummaryAfterPayment(studentId, feeId, academicYearId, groupId);
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a payment summary (soft delete).
     * 
     * @param summaryId The payment summary ID
     * @return Success response
     */
    @Operation(summary = "Delete payment summary",
               description = "Soft delete a payment summary (sets end_at timestamp)")
    @ApiResponse(responseCode = "204", description = "Payment summary deleted successfully")
    @ApiResponse(responseCode = "404", description = "Payment summary not found")
    @DeleteMapping("/summary/{summaryId}")
    public ResponseEntity<Void> deletePaymentSummary(
            @Parameter(description = "Payment summary ID", example = "1")
            @PathVariable Integer summaryId) {
        
        log.info("Deleting payment summary with ID: {}", summaryId);
        
        studentPaymentService.deletePaymentSummary(summaryId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Recalculate all payment summaries.
     * This is a maintenance endpoint that should be used carefully.
     * 
     * @return Success response
     */
    @Operation(summary = "Recalculate all payment summaries",
               description = "Recalculate totals and status for all payment summaries (maintenance operation)")
    @ApiResponse(responseCode = "200", description = "Payment summaries recalculated successfully")
    @PostMapping("/recalculate-all")
    public ResponseEntity<Void> recalculateAllPaymentSummaries() {
        
        log.info("Starting recalculation of all payment summaries");
        
        studentPaymentService.recalculateAllPaymentSummaries();
        return ResponseEntity.ok().build();
    }
}