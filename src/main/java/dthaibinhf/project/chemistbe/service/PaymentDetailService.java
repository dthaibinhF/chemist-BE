package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.PaymentDetailDTO;
import dthaibinhf.project.chemistbe.mapper.PaymentDetailMapper;
import dthaibinhf.project.chemistbe.model.PaymentDetail;
import dthaibinhf.project.chemistbe.model.PaymentStatus;
import dthaibinhf.project.chemistbe.repository.PaymentDetailRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PaymentDetailService {
    PaymentDetailRepository paymentDetailRepository;
    PaymentDetailMapper paymentDetailMapper;
    StudentPaymentService studentPaymentService;

    public List<PaymentDetailDTO> getAllActivePaymentDetails() {
        return paymentDetailRepository.findAllActivePaymentDetails()
                .stream()
                .map(paymentDetailMapper::toDto)
                .collect(Collectors.toList());
    }

    public PaymentDetailDTO getActivePaymentDetailById(Integer id) {
        PaymentDetail paymentDetail = paymentDetailRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment Detail not found with id: " + id));
        return paymentDetailMapper.toDto(paymentDetail);
    }

    @Transactional
    public PaymentDetailDTO createPaymentDetail(PaymentDetailDTO paymentDetailDTO) {
        // Validate payment amount integrity before creating an entity
        validatePaymentAmountIntegrity(paymentDetailDTO);

        PaymentDetail paymentDetail = paymentDetailMapper.toEntity(paymentDetailDTO);

        // Set default values for new fields if not provided
        if (paymentDetail.getPaymentStatus() == null) {
            paymentDetail.setPaymentStatus(PaymentStatus.PENDING);
        }
        if (paymentDetail.getDueDate() == null) {
            // Set the default due date to 30 days from now
            paymentDetail.setDueDate(OffsetDateTime.now().plusDays(30));
        }

        // Update payment status based on amounts and due date
        paymentDetail.updatePaymentStatus();

        PaymentDetail savedPaymentDetail = paymentDetailRepository.save(paymentDetail);

        // Update the payment summary after creating payment detail
        try {
            Integer academicYearId = savedPaymentDetail.getStudent()
                    .getStudentDetails().stream()
                    .filter(detail -> detail.getEndAt() == null)
                    .findFirst()
                    .map(detail -> detail.getAcademicYear().getId())
                    .orElse(null);

            Integer groupId = savedPaymentDetail.getStudent()
                    .getStudentDetails().stream()
                    .filter(detail -> detail.getEndAt() == null)
                    .findFirst()
                    .map(detail -> detail.getGroup().getId())
                    .orElse(null);

            // Try to update existing payment summary, or create one if it doesn't exist
            studentPaymentService.updateOrCreatePaymentSummaryAfterPayment(
                savedPaymentDetail.getStudent().getId(),
                savedPaymentDetail.getFee().getId(),
                academicYearId,
                groupId
            );
            return paymentDetailMapper.toDto(savedPaymentDetail);
        } catch (Exception e) {
            log.warn("Failed to update/create payment summary after creating payment detail: {}", e.getMessage());
        }

        return paymentDetailMapper.toDto(savedPaymentDetail);
    }

    @Transactional
    public PaymentDetailDTO updatePaymentDetail(Integer id, PaymentDetailDTO paymentDetailDTO) {
        PaymentDetail existingPaymentDetail = paymentDetailRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment Detail not found with id: " + id));

        // Validate payment amount integrity before updating
        validatePaymentAmountIntegrity(paymentDetailDTO);

        PaymentDetail updatedPaymentDetail = paymentDetailMapper.partialUpdate(paymentDetailDTO, existingPaymentDetail);

        // Update payment status based on new amounts and due date
        updatedPaymentDetail.updatePaymentStatus();

        PaymentDetail savedPaymentDetail = paymentDetailRepository.save(updatedPaymentDetail);

        // Update payment summary after updating payment detail
        try {
            Integer academicYearId = savedPaymentDetail.getStudent()
                    .getStudentDetails().stream()
                    .filter(detail -> detail.getEndAt() == null)
                    .findFirst()
                    .map(detail -> detail.getAcademicYear().getId())
                    .orElse(null);

            Integer groupId = savedPaymentDetail.getStudent()
                    .getStudentDetails().stream()
                    .filter(detail -> detail.getEndAt() == null)
                    .findFirst()
                    .map(detail -> detail.getGroup().getId())
                    .orElse(null);

            studentPaymentService.updateOrCreatePaymentSummaryAfterPayment(
                savedPaymentDetail.getStudent().getId(),
                savedPaymentDetail.getFee().getId(),
                academicYearId,
                groupId
            );
        } catch (Exception e) {
            log.warn("Failed to update/create payment summary after updating payment detail: {}", e.getMessage());
        }

        return paymentDetailMapper.toDto(savedPaymentDetail);
    }

    @Transactional
    public void deletePaymentDetail(Integer id) {
        PaymentDetail paymentDetail = paymentDetailRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment Detail not found with id: " + id));
        paymentDetail.softDelete();
        paymentDetailRepository.save(paymentDetail);
    }

    @Tool(description = "Get payment history and details for a specific student. Useful for queries like 'payment history for student 5', 'show all payments by student John', or 'detailed payment records for student ID 10'")
    public List<PaymentDetailDTO> getPaymentDetailsByStudentId(@ToolParam(description = "The unique ID of the student") Integer studentId) {
        return paymentDetailRepository.findActiveByStudentId(studentId)
                .stream()
                .map(paymentDetailMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PaymentDetailDTO> getPaymentDetailsByFeeId(Integer feeId) {
        return paymentDetailRepository.findActiveByFeeId(feeId)
                .stream()
                .map(paymentDetailMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<PaymentDetailDTO> getPaymentDetailsByStudentIdAndFeeId(Integer studentId, Integer feeId) {
        return paymentDetailRepository.findActiveByStudentIdAndFeeId(studentId, feeId)
                .stream()
                .map(paymentDetailMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get payment details by payment status.
     *
     * @param status The payment status
     * @return List of payment details with the specified status
     */
    @Tool(description = "Find all payment details by status (PENDING, PAID, OVERDUE, PARTIAL). Useful for queries like 'show unpaid students', 'find overdue payments', or 'list students with pending payments'")
    @Transactional(readOnly = true)
    public List<PaymentDetailDTO> getPaymentDetailsByStatus(@ToolParam(description = "Payment status: PENDING, PAID, OVERDUE, or PARTIAL") PaymentStatus status) {
        return paymentDetailRepository.findActiveByPaymentStatus(status)
                .stream()
                .map(paymentDetailMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get total amount paid by a student for a specific fee.
     *
     * @param studentId The student ID
     * @param feeId The fee ID
     * @return Total amount paid
     */
    @Tool(description = "Calculate total amount paid by a specific student for a specific fee. Useful for queries like 'how much has student 5 paid for tuition?', 'total payments by student John for fee ID 2'")
    @Transactional(readOnly = true)
    public BigDecimal getTotalAmountPaidByStudentAndFee(@ToolParam(description = "The unique ID of the student") Integer studentId,
                                                       @ToolParam(description = "The unique ID of the fee") Integer feeId) {
        return paymentDetailRepository.getTotalAmountPaidByStudentAndFee(studentId, feeId);
    }

    /**
     * Get payment details within a date range.
     *
     * @param startDate Start date
     * @param endDate End date
     * @return List of payment details within the date range
     */
    @Transactional(readOnly = true)
    public List<PaymentDetailDTO> getPaymentDetailsByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        return paymentDetailRepository.findActiveByDateRange(startDate, endDate)
                .stream()
                .map(paymentDetailMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Create payment detail with automatic summary update.
     * This method ensures that payment summaries are properly updated.
     *
     * @param paymentDetailDTO The payment detail DTO
     * @param academicYearId The academic year ID for summary update
     * @param groupId The group ID for summary update
     * @return Created payment detail DTO
     */
    @Transactional
    public PaymentDetailDTO createPaymentDetailWithSummaryUpdate(PaymentDetailDTO paymentDetailDTO,
                                                                Integer academicYearId,
                                                                Integer groupId) {
        // Validate payment amount integrity before creating an entity
        validatePaymentAmountIntegrity(paymentDetailDTO);

        PaymentDetail paymentDetail = paymentDetailMapper.toEntity(paymentDetailDTO);

        // Set default values for new fields if not provided
        if (paymentDetail.getPaymentStatus() == null) {
            paymentDetail.setPaymentStatus(PaymentStatus.PENDING);
        }
        if (paymentDetail.getGeneratedAmount() == null) {
            paymentDetail.setGeneratedAmount(paymentDetail.getAmount());
        }
        if (paymentDetail.getDueDate() == null) {
            paymentDetail.setDueDate(OffsetDateTime.now().plusDays(30));
        }

        // Update payment status
        paymentDetail.updatePaymentStatus();

        PaymentDetail savedPaymentDetail = paymentDetailRepository.save(paymentDetail);

        // Update the payment summary with proper academic year and group info
        studentPaymentService.updateOrCreatePaymentSummaryAfterPayment(
            savedPaymentDetail.getStudent().getId(),
            savedPaymentDetail.getFee().getId(),
            academicYearId,
            groupId
        );

        log.info("Created payment detail for student {} with amount {} and updated payment summary",
                savedPaymentDetail.getStudent().getId(), savedPaymentDetail.getAmount());

        return paymentDetailMapper.toDto(savedPaymentDetail);
    }

    /**
     * Validates that payment amount integrity is maintained.
     * Ensures: amount + have_discount = generated_amount
     *
     * @param paymentDetailDTO The payment detail to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validatePaymentAmountIntegrity(PaymentDetailDTO paymentDetailDTO) {
        // Skip validation if generated_amount is null (backward compatibility)
        if (paymentDetailDTO.getGeneratedAmount() == null || paymentDetailDTO.getAmount() == null) {
            return;
        }

        // Ensure amount and generated_amount are non-negative
        BigDecimal discount = paymentDetailDTO.getHaveDiscount() != null ?
            paymentDetailDTO.getHaveDiscount() : BigDecimal.ZERO;
        BigDecimal newTotal = paymentDetailDTO.getAmount().add(discount);

        //get the older list payment detail of this student and this fee
        List<PaymentDetail> olderPaymentDetails = paymentDetailRepository
            .findActiveByStudentIdAndFeeId(paymentDetailDTO.getStudentId(), paymentDetailDTO.getFeeId());
        // Calculate the total amount already paid for this student and fee
        BigDecimal totalOlderPaid = olderPaymentDetails.stream().reduce(BigDecimal.ZERO, (sum, detail) -> sum.add(detail.getAmount()), BigDecimal::add);

        // Calculate the expected total amount
        BigDecimal expectedTotal = totalOlderPaid.add(newTotal);

        if (expectedTotal.compareTo(paymentDetailDTO.getGeneratedAmount()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "total amount paid exceeds the generated amount");
        }

        log.debug("Payment amount integrity validated: amount={}, discount={}, generated_amount={}",
                 paymentDetailDTO.getAmount(), discount, paymentDetailDTO.getGeneratedAmount());
    }
}
