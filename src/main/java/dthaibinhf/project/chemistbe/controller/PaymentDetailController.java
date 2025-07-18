package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.PaymentDetailDTO;
import dthaibinhf.project.chemistbe.service.PaymentDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment-detail")
@RequiredArgsConstructor
public class PaymentDetailController {
    private final PaymentDetailService paymentDetailService;

    @GetMapping
    public ResponseEntity<List<PaymentDetailDTO>> getAllActivePaymentDetails() {
        return ResponseEntity.ok(paymentDetailService.getAllActivePaymentDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentDetailDTO> getPaymentDetailById(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentDetailService.getActivePaymentDetailById(id));
    }

    @PostMapping
    public ResponseEntity<PaymentDetailDTO> createPaymentDetail(@Valid @RequestBody PaymentDetailDTO paymentDetailDTO) {
        return new ResponseEntity<>(paymentDetailService.createPaymentDetail(paymentDetailDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentDetailDTO> updatePaymentDetail(
            @PathVariable Integer id,
            @Valid @RequestBody PaymentDetailDTO paymentDetailDTO) {
        return ResponseEntity.ok(paymentDetailService.updatePaymentDetail(id, paymentDetailDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentDetail(@PathVariable Integer id) {
        paymentDetailService.deletePaymentDetail(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PaymentDetailDTO>> getPaymentDetailsByStudentId(@PathVariable Integer studentId) {
        return ResponseEntity.ok(paymentDetailService.getPaymentDetailsByStudentId(studentId));
    }

    @GetMapping("/fee/{feeId}")
    public ResponseEntity<List<PaymentDetailDTO>> getPaymentDetailsByFeeId(@PathVariable Integer feeId) {
        return ResponseEntity.ok(paymentDetailService.getPaymentDetailsByFeeId(feeId));
    }

    @GetMapping("/student/{studentId}/fee/{feeId}")
    public ResponseEntity<List<PaymentDetailDTO>> getPaymentDetailsByStudentIdAndFeeId(
            @PathVariable Integer studentId,
            @PathVariable Integer feeId) {
        return ResponseEntity.ok(paymentDetailService.getPaymentDetailsByStudentIdAndFeeId(studentId, feeId));
    }
}
