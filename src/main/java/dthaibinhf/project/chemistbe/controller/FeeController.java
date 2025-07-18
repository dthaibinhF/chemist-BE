package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.FeeDTO;
import dthaibinhf.project.chemistbe.service.FeeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fee")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class FeeController {

    FeeService feeService;
    @PostMapping
    public ResponseEntity<FeeDTO> createFee(@Valid @RequestBody FeeDTO feeDTO) {
        return ResponseEntity.ok(feeService.createFee(feeDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeeDTO> getFee(@PathVariable Integer id) {
        return ResponseEntity.ok(feeService.getFeeById(id));
    }

    @GetMapping
    public ResponseEntity<List<FeeDTO>> getAllFees() {
        return ResponseEntity.ok(feeService.getAllFees());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeeDTO> updateFee(@PathVariable Integer id, @Valid @RequestBody FeeDTO feeDTO) {
        return ResponseEntity.ok(feeService.updateFee(id, feeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFee(@PathVariable Integer id) {
        feeService.deleteFee(id);
        return ResponseEntity.noContent().build();
    }
}