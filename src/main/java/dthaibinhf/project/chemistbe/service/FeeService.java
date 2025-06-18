package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.FeeDTO;
import dthaibinhf.project.chemistbe.mapper.FeeMapper;
import dthaibinhf.project.chemistbe.model.Fee;
import dthaibinhf.project.chemistbe.repository.FeeRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class FeeService {

    FeeRepository feeRepository;
    FeeMapper feeMapper;

    public List<FeeDTO> getAllFees() {
        return feeRepository.findAllActiveFees().stream()
                .map(feeMapper::toDto)
                .collect(Collectors.toList());
    }

    public FeeDTO getFeeById(Integer id) {
        Fee fee = feeRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee not found: " + id));
        return feeMapper.toDto(fee);
    }

    @Transactional
    public FeeDTO createFee(@Valid FeeDTO feeDTO) {
        Fee fee = feeMapper.toEntity(feeDTO);
        fee.setId(null);
        Fee savedFee = feeRepository.save(fee);
        return feeMapper.toDto(savedFee);
    }

    @Transactional
    public FeeDTO updateFee(Integer id, @Valid FeeDTO feeDTO) {
        Fee fee = feeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee not found: " + id));
        feeMapper.partialUpdate(feeDTO, fee);
        Fee updatedFee = feeRepository.save(fee);
        return feeMapper.toDto(updatedFee);
    }

    @Transactional
    @CacheEvict(value = {"fees", "allFees"}, key = "#id")
    public void deleteFee(Integer id) {
        Fee fee = feeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fee not found: " + id));
        fee.softDelete();
        feeRepository.save(fee);
    }
}