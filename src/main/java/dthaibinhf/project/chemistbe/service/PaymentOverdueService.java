package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.PaymentDetailDTO;
import dthaibinhf.project.chemistbe.dto.StudentPaymentSummaryDTO;
import dthaibinhf.project.chemistbe.mapper.PaymentDetailMapper;
import dthaibinhf.project.chemistbe.mapper.StudentPaymentSummaryMapper;
import dthaibinhf.project.chemistbe.model.PaymentDetail;
import dthaibinhf.project.chemistbe.model.PaymentStatus;
import dthaibinhf.project.chemistbe.model.StudentPaymentSummary;
import dthaibinhf.project.chemistbe.repository.PaymentDetailRepository;
import dthaibinhf.project.chemistbe.repository.StudentPaymentSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for detecting and managing overdue payments.
 * 
 * This service provides functionality to:
 * - Detect overdue payments and summaries
 * - Automatically update payment statuses
 * - Generate overdue payment reports
 * - Send notifications for overdue payments (future enhancement)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentOverdueService {
    
    private final PaymentDetailRepository paymentDetailRepository;
    private final StudentPaymentSummaryRepository summaryRepository;
    private final PaymentDetailMapper paymentDetailMapper;
    private final StudentPaymentSummaryMapper summaryMapper;
    
    /**
     * Get all overdue payment details.
     * 
     * @return List of overdue payment detail DTOs
     */
    @Transactional(readOnly = true)
    public List<PaymentDetailDTO> getOverduePaymentDetails() {
        OffsetDateTime currentDate = OffsetDateTime.now();
        
        List<PaymentDetail> overduePayments = paymentDetailRepository.findOverduePayments(currentDate);
        
        return overduePayments.stream()
                .map(paymentDetailMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all overdue payment summaries.
     * 
     * @return List of overdue payment summary DTOs
     */
    @Transactional(readOnly = true)
    public List<StudentPaymentSummaryDTO> getOverduePaymentSummaries() {
        OffsetDateTime currentDate = OffsetDateTime.now();
        
        List<StudentPaymentSummary> overdueSummaries = summaryRepository.findOverduePayments(currentDate);
        
        return overdueSummaries.stream()
                .map(summaryMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get overdue payments for a specific student.
     * 
     * @param studentId The student ID
     * @return List of overdue payment summary DTOs for the student
     */
    @Transactional(readOnly = true)
    public List<StudentPaymentSummaryDTO> getOverduePaymentsForStudent(Integer studentId) {
        OffsetDateTime currentDate = OffsetDateTime.now();
        
        List<StudentPaymentSummary> studentSummaries = summaryRepository.findActiveByStudentId(studentId);
        
        return studentSummaries.stream()
                .filter(summary -> summary.getDueDate() != null && 
                                 summary.getDueDate().isBefore(currentDate) &&
                                 summary.getPaymentStatus() != PaymentStatus.PAID)
                .map(summaryMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Update payment statuses for all overdue payments.
     * This method should be run periodically (e.g., daily) to update payment statuses.
     * 
     * @return Number of payments updated to overdue status
     */
    public int updateOverduePaymentStatuses() {
        log.info("Starting automatic update of overdue payment statuses");
        
        OffsetDateTime currentDate = OffsetDateTime.now();
        int updatedCount = 0;
        
        // Update payment details
        List<PaymentDetail> overdueDetails = paymentDetailRepository.findOverduePayments(currentDate);
        for (PaymentDetail detail : overdueDetails) {
            if (detail.getPaymentStatus() != PaymentStatus.OVERDUE) {
                detail.setPaymentStatus(PaymentStatus.OVERDUE);
                paymentDetailRepository.save(detail);
                updatedCount++;
            }
        }
        
        // Update payment summaries
        List<StudentPaymentSummary> overdueSummaries = summaryRepository.findOverduePayments(currentDate);
        for (StudentPaymentSummary summary : overdueSummaries) {
            if (summary.getPaymentStatus() != PaymentStatus.OVERDUE) {
                summary.setPaymentStatus(PaymentStatus.OVERDUE);
                summaryRepository.save(summary);
                updatedCount++;
            }
        }
        
        log.info("Updated {} payments to overdue status", updatedCount);
        
        return updatedCount;
    }
    
    /**
     * Get overdue payment statistics.
     * 
     * @return Overdue payment statistics
     */
    @Transactional(readOnly = true)
    public OverdueStatistics getOverdueStatistics() {
        OffsetDateTime currentDate = OffsetDateTime.now();
        
        List<StudentPaymentSummary> overdueSummaries = summaryRepository.findOverduePayments(currentDate);
        List<PaymentDetail> overdueDetails = paymentDetailRepository.findOverduePayments(currentDate);
        
        java.math.BigDecimal totalOverdueAmount = overdueSummaries.stream()
                .map(StudentPaymentSummary::getOutstandingAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        
        int uniqueStudentsWithOverduePayments = (int) overdueSummaries.stream()
                .map(summary -> summary.getStudent().getId())
                .distinct()
                .count();
        
        return OverdueStatistics.builder()
                .totalOverdueAmount(totalOverdueAmount)
                .overduePaymentSummariesCount(overdueSummaries.size())
                .overduePaymentDetailsCount(overdueDetails.size())
                .uniqueStudentsWithOverduePayments(uniqueStudentsWithOverduePayments)
                .asOfDate(currentDate)
                .build();
    }
    
    /**
     * Check if a specific student has any overdue payments.
     * 
     * @param studentId The student ID
     * @return true if the student has overdue payments
     */
    @Transactional(readOnly = true)
    public boolean hasOverduePayments(Integer studentId) {
        OffsetDateTime currentDate = OffsetDateTime.now();
        
        List<StudentPaymentSummary> studentSummaries = summaryRepository.findActiveByStudentId(studentId);
        
        return studentSummaries.stream()
                .anyMatch(summary -> summary.getDueDate() != null && 
                                   summary.getDueDate().isBefore(currentDate) &&
                                   summary.getPaymentStatus() != PaymentStatus.PAID);
    }
    
    /**
     * Get the number of days a payment is overdue.
     * 
     * @param summaryId The payment summary ID
     * @return Number of days overdue (negative if not overdue)
     */
    @Transactional(readOnly = true)
    public long getDaysOverdue(Integer summaryId) {
        StudentPaymentSummary summary = summaryRepository.findActiveById(summaryId)
                .orElseThrow(() -> new IllegalArgumentException("Payment summary not found: " + summaryId));
        
        if (summary.getDueDate() == null) {
            return -1; // No due date set
        }
        
        OffsetDateTime currentDate = OffsetDateTime.now();
        if (summary.getDueDate().isAfter(currentDate)) {
            return -1; // Not overdue yet
        }
        
        return java.time.Duration.between(summary.getDueDate(), currentDate).toDays();
    }
    
    /**
     * Static inner class for overdue payment statistics.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    public static class OverdueStatistics {
        private java.math.BigDecimal totalOverdueAmount;
        private int overduePaymentSummariesCount;
        private int overduePaymentDetailsCount;
        private int uniqueStudentsWithOverduePayments;
        private OffsetDateTime asOfDate;
    }
}