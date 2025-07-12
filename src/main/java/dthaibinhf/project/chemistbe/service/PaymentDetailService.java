package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.PaymentDetailDTO;
import dthaibinhf.project.chemistbe.mapper.PaymentDetailMapper;
import dthaibinhf.project.chemistbe.model.PaymentDetail;
import dthaibinhf.project.chemistbe.repository.PaymentDetailRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PaymentDetailService {
    PaymentDetailRepository paymentDetailRepository;
    PaymentDetailMapper paymentDetailMapper;

    @Transactional(readOnly = true)
    public List<PaymentDetailDTO> getAllActivePaymentDetails() {
        return paymentDetailRepository.findAllActivePaymentDetails()
                .stream()
                .map(paymentDetailMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentDetailDTO getActivePaymentDetailById(Integer id) {
        PaymentDetail paymentDetail = paymentDetailRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment Detail not found with id: " + id));
        return paymentDetailMapper.toDto(paymentDetail);
    }

    @Transactional
    public PaymentDetailDTO createPaymentDetail(PaymentDetailDTO paymentDetailDTO) {
        PaymentDetail paymentDetail = paymentDetailMapper.toEntity(paymentDetailDTO);
        PaymentDetail savedPaymentDetail = paymentDetailRepository.save(paymentDetail);
        return paymentDetailMapper.toDto(savedPaymentDetail);
    }

    @Transactional
    public PaymentDetailDTO updatePaymentDetail(Integer id, PaymentDetailDTO paymentDetailDTO) {
        PaymentDetail existingPaymentDetail = paymentDetailRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment Detail not found with id: " + id));

        PaymentDetail updatedPaymentDetail = paymentDetailMapper.partialUpdate(paymentDetailDTO, existingPaymentDetail);
        PaymentDetail savedPaymentDetail = paymentDetailRepository.save(updatedPaymentDetail);
        return paymentDetailMapper.toDto(savedPaymentDetail);
    }

    @Transactional
    public void deletePaymentDetail(Integer id) {
        PaymentDetail paymentDetail = paymentDetailRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment Detail not found with id: " + id));
        paymentDetail.softDelete();
        paymentDetailRepository.save(paymentDetail);
    }

    @Transactional(readOnly = true)
    public List<PaymentDetailDTO> getPaymentDetailsByStudentId(Integer studentId) {
        return paymentDetailRepository.findActiveByStudentId(studentId)
                .stream()
                .map(paymentDetailMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentDetailDTO> getPaymentDetailsByFeeId(Integer feeId) {
        return paymentDetailRepository.findActiveByFeeId(feeId)
                .stream()
                .map(paymentDetailMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentDetailDTO> getPaymentDetailsByStudentIdAndFeeId(Integer studentId, Integer feeId) {
        return paymentDetailRepository.findActiveByStudentIdAndFeeId(studentId, feeId)
                .stream()
                .map(paymentDetailMapper::toDto)
                .collect(Collectors.toList());
    }
}
