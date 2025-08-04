package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.StudentPaymentSummaryDTO;
import dthaibinhf.project.chemistbe.mapper.StudentPaymentSummaryMapper;
import dthaibinhf.project.chemistbe.model.*;
import dthaibinhf.project.chemistbe.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing student payment obligations and automatic fee calculations.
 * 
 * This service handles:
 * - Automatic payment obligation generation when students enroll in groups
 * - Bulk payment generation for entire groups
 * - Payment status tracking and updates
 * - Pro-rata calculations for mid-period enrollments
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudentPaymentService {
    
    private final StudentPaymentSummaryRepository summaryRepository;
    private final PaymentDetailRepository paymentDetailRepository;
    private final StudentRepository studentRepository;
    private final FeeRepository feeRepository;
    private final GroupRepository groupRepository;
    private final AcademicYearRepository academicYearRepository;
    private final StudentPaymentSummaryMapper summaryMapper;
    
    // Default payment terms (30 days from enrollment)
    private static final int DEFAULT_PAYMENT_DAYS = 30;
    
    /**
     * Generate payment obligation for a student joining a group.
     * 
     * @param studentId The ID of the student
     * @param groupId The ID of the group
     * @return Created payment summary DTO
     */
    public StudentPaymentSummaryDTO generatePaymentForStudentInGroup(Integer studentId, Integer groupId) {
        log.info("Generating payment obligation for student {} in group {}", studentId, groupId);
        BigDecimal totalPaid;
        BigDecimal totalDiscount = BigDecimal.ZERO; // Initialize the total discount if needed

        // Validate and fetch entities
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));

        List<PaymentDetail> existingDetails = paymentDetailRepository.findByStudentIdAndGroupId(studentId, groupId);

        if (existingDetails.isEmpty()) {
            log.info("Payment details already exist for student {} in group {}", studentId, groupId);
            totalPaid = new BigDecimal(0);
        } else {
            totalPaid  = existingDetails.stream().map(PaymentDetail::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            totalDiscount = existingDetails.stream().map(PaymentDetail::getHaveDiscount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            log.info("Total amount already paid by student {} in group {}: {}", studentId, groupId, totalPaid);
            log.info("Total discount for student {} in group {}: {}", studentId, groupId, totalDiscount);

        }
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with ID: " + groupId));
        
        Fee fee = group.getFee();
        AcademicYear academicYear = group.getAcademicYear();
        
        // Check if the payment summary already exists
        if (summaryRepository.existsByStudentFeeAcademicYearAndGroup(studentId, fee.getId(), 
                academicYear.getId(), groupId)) {
            throw new IllegalStateException("Payment summary already exists for student " + studentId + 
                                          " in group " + groupId);
        }
        
        // Calculate the payment amount (with pro-rata if needed)
        OffsetDateTime dueDate = calculateDueDate();
        BigDecimal amountDue = fee.getAmount();
        BigDecimal outStandingPaid = amountDue.subtract(totalPaid).subtract(totalDiscount);

        // Create a payment summary
        StudentPaymentSummary summary = StudentPaymentSummary.builder()
                .student(student)
                .fee(fee)
                .academicYear(academicYear)
                .group(group)
                .totalAmountDue(amountDue)
                .totalAmountPaid(totalPaid)
                .outstandingAmount(outStandingPaid)
                .paymentStatus(outStandingPaid.compareTo(BigDecimal.ZERO) > 0 ? PaymentStatus.PENDING : PaymentStatus.PAID)
                .dueDate(dueDate)
                .enrollmentDate(OffsetDateTime.now())
                .build();
        
        StudentPaymentSummary savedSummary = summaryRepository.save(summary);
        
        log.info("Created payment summary for student {} with amount due: {}", studentId, amountDue);
        
        return summaryMapper.toDto(savedSummary);
    }
    
    /**
     * Generate payment obligations for all students in a group.
     * 
     * @param groupId The ID of the group
     * @return List of created payment summary DTOs
     */
    public List<StudentPaymentSummaryDTO> generatePaymentsForGroup(Integer groupId) {
        log.info("Generating payment obligations for all students in group {}", groupId);
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with ID: " + groupId));
        
        // Get all students in the group through StudentDetail
        List<Student> studentsInGroup = studentRepository.findByGroupIdWithNewestActiveDetails(groupId);
        
        return studentsInGroup.stream()
                .map(student -> {
                    try {
                        // Check if the payment summary already exists
                        if (!summaryRepository.existsByStudentFeeAcademicYearAndGroup(
                                student.getId(), group.getFee().getId(), 
                                group.getAcademicYear().getId(), groupId)) {
                            return generatePaymentForStudentInGroup(student.getId(), groupId);
                        } else {
                            log.debug("Payment summary already exists for student {} in group {}", 
                                    student.getId(), groupId);
                            return null;
                        }
                    } catch (Exception e) {
                        log.error("Failed to generate payment for student {} in group {}: {}", 
                                student.getId(), groupId, e.getMessage());
                        return null;
                    }
                })
                .filter(summary -> summary != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Update payment summary after a payment is made.
     * 
     * @param studentId The student ID
     * @param feeId The fee ID
     * @param academicYearId The academic year ID
     * @param groupId The group ID (optional)
     */
    public void updatePaymentSummaryAfterPayment(Integer studentId, Integer feeId, 
                                                Integer academicYearId, Integer groupId) {
        log.info("Updating payment summary for student {} after payment", studentId);
        
        StudentPaymentSummary summary = summaryRepository
                .findActiveByStudentFeeAcademicYearAndGroup(studentId, feeId, academicYearId, groupId)
                .orElseThrow(() -> new EntityNotFoundException("Payment summary not found"));
        
        // Calculate total amount paid from all payment details
        BigDecimal totalPaid = paymentDetailRepository.getTotalAmountPaidByStudentAndFee(studentId, feeId);
        
        // Update summary
        summary.setTotalAmountPaid(totalPaid);
        summary.recalculateStatus();
        
        summaryRepository.save(summary);
        
        log.info("Updated payment summary for student {}: paid={}, status={}", 
                studentId, totalPaid, summary.getPaymentStatus());
    }
    
    /**
     * Update payment summary after a payment is made, or create one if it doesn't exist.
     * This method handles the case where a payment detail is created before a payment summary exists.
     * 
     * @param studentId The student ID
     * @param feeId The fee ID
     * @param academicYearId The academic year ID
     * @param groupId The group ID (optional)
     */
    public void updateOrCreatePaymentSummaryAfterPayment(Integer studentId, Integer feeId, 
                                                        Integer academicYearId, Integer groupId) {
        log.info("Updating or creating payment summary for student {} after payment", studentId);
        
        // Try to find an existing summary
        var existingSummary = summaryRepository
                .findActiveByStudentFeeAcademicYearAndGroup(studentId, feeId, academicYearId, groupId);
        
        if (existingSummary.isPresent()) {
            // Update existing summary
            StudentPaymentSummary summary = existingSummary.get();
            BigDecimal totalPaid = paymentDetailRepository.getTotalAmountPaidByStudentAndFee(studentId, feeId);
            
            summary.setTotalAmountPaid(totalPaid);
            summary.recalculateStatus();
            summaryRepository.save(summary);
            
            log.info("Updated existing payment summary for student {}: paid={}, status={}", 
                    studentId, totalPaid, summary.getPaymentStatus());
        } else {
            // Create a new summary if groupId is provided
            if (groupId != null) {
                try {
                    generatePaymentForStudentInGroup(studentId, groupId);
                    log.info("Created new payment summary for student {} in group {}", studentId, groupId);
                    
                    // Now update the newly created summary
                    var newSummary = summaryRepository
                            .findActiveByStudentFeeAcademicYearAndGroup(studentId, feeId, academicYearId, groupId);
                    
                    if (newSummary.isPresent()) {
                        StudentPaymentSummary summary = newSummary.get();
                        BigDecimal totalPaid = paymentDetailRepository.getTotalAmountPaidByStudentAndFee(studentId, feeId);
                        
                        summary.setTotalAmountPaid(totalPaid);
                        summary.recalculateStatus();
                        summaryRepository.save(summary);
                        
                        log.info("Updated newly created payment summary for student {}: paid={}, status={}", 
                                studentId, totalPaid, summary.getPaymentStatus());
                    }
                } catch (Exception e) {
                    log.warn("Failed to create payment summary for student {} in group {}: {}", 
                            studentId, groupId, e.getMessage());
                }
            } else {
                log.warn("Cannot create payment summary for student {} - no group ID provided", studentId);
            }
        }
    }
    
    /**
     * Get all payment summaries for a student.
     * 
     * @param studentId The student ID
     * @return List of payment summary DTOs
     */
    @Tool(description = "Get payment summary and status for a specific student. Useful for queries like 'has student John paid?', 'payment status for student ID 5', or 'how much does student 10 owe?'")
    @Transactional(readOnly = true)
    public List<StudentPaymentSummaryDTO> getStudentPaymentSummaries(@ToolParam(description = "The unique ID of the student") Integer studentId) {
        return summaryRepository.findActiveByStudentId(studentId)
                .stream()
                .map(summaryMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all payment summaries for a group.
     * 
     * @param groupId The group ID
     * @return List of payment summary DTOs
     */
    @Tool(description = "Get payment summaries for all students in a specific group/class. Useful for queries like 'payment status for group 5', 'who hasn't paid in class 10', or 'show payment overview for group ID 3'")
    @Transactional(readOnly = true)
    public List<StudentPaymentSummaryDTO> getGroupPaymentSummaries(@ToolParam(description = "The unique ID of the group or class") Integer groupId) {
        return summaryRepository.findActiveByGroupId(groupId)
                .stream()
                .map(summaryMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get payment summary by ID.
     * 
     * @param summaryId The summary ID
     * @return Payment summary DTO
     */
    @Transactional(readOnly = true)
    public StudentPaymentSummaryDTO getPaymentSummaryById(Integer summaryId) {
        StudentPaymentSummary summary = summaryRepository.findActiveById(summaryId)
                .orElseThrow(() -> new EntityNotFoundException("Payment summary not found with ID: " + summaryId));
        return summaryMapper.toDto(summary);
    }
    
    /**
     * Delete payment summary (soft delete).
     * 
     * @param summaryId The summary ID
     */
    public void deletePaymentSummary(Integer summaryId) {
        StudentPaymentSummary summary = summaryRepository.findActiveById(summaryId)
                .orElseThrow(() -> new EntityNotFoundException("Payment summary not found with ID: " + summaryId));
        
        summary.softDelete();
        summaryRepository.save(summary);
        
        log.info("Deleted payment summary with ID: {}", summaryId);
    }
    
    /**
     * Calculate amount due for a fee, considering pro-rata calculations.
     * Currently returns the full fee amount, but can be enhanced for pro-rata.
     * 
     * @param fee The fee structure
     * @param academicYear The academic year
     * @return Amount due
     */
    private BigDecimal calculateAmountDue(Fee fee, AcademicYear academicYear) {
        // For now, return full fee amount
        // TODO: Implement pro-rata calculation based on enrollment date vs academic year period
        return fee.getAmount();
    }
    
    /**
     * Calculate due date for payment (30 days from now by default).
     * 
     * @return Due date
     */
    private OffsetDateTime calculateDueDate() {
        return OffsetDateTime.now().plusDays(DEFAULT_PAYMENT_DAYS);
    }
    
    /**
     * Recalculate all payment summaries for updated amounts or status.
     * This is useful for batch processing or system maintenance.
     */
    public void recalculateAllPaymentSummaries() {
        log.info("Starting recalculation of all payment summaries");
        
        List<StudentPaymentSummary> allSummaries = summaryRepository.findAllActive();
        
        for (StudentPaymentSummary summary : allSummaries) {
            try {
                // Recalculate total paid amount
                BigDecimal totalPaid = paymentDetailRepository
                        .getTotalAmountPaidByStudentAndFee(summary.getStudent().getId(), summary.getFee().getId());
                
                summary.setTotalAmountPaid(totalPaid);
                summary.recalculateStatus();
                
                summaryRepository.save(summary);
                
            } catch (Exception e) {
                log.error("Failed to recalculate summary for student {} and fee {}: {}", 
                        summary.getStudent().getId(), summary.getFee().getId(), e.getMessage());
            }
        }
        
        log.info("Completed recalculation of {} payment summaries", allSummaries.size());
    }

    /**
     * Get payment summaries by fee and student.
     *
     * @param feeId The fee ID
     * @param studentId The student ID
     * @return Payment summary DTO
     */
    @Tool(description = "Get payment summary for a specific student and fee. Useful for queries like 'what is the payment status for student 5 with fee ID 3?' or 'how much does student 10 owe for fee 2?'")
    @Transactional(readOnly = true)
    public StudentPaymentSummaryDTO getPaymentSummariesByFeeAndStudent(Integer feeId, Integer studentId) {
        log.info("Getting payment summary for student {} with fee {}", studentId, feeId);

        // Validate fee and student existence
        feeRepository.findById(feeId)
                .orElseThrow(() -> new EntityNotFoundException("Fee not found with ID: " + feeId));
        studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));

        StudentPaymentSummary summary = summaryRepository.findActiveByFeeAndStudent(feeId, studentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment summary not found for student " + studentId + " with fee " + feeId));
        return summaryMapper.toDto(summary);
    }
}