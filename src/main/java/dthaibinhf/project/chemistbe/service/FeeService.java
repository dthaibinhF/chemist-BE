package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.FeeDTO;
import dthaibinhf.project.chemistbe.mapper.FeeMapper;
import dthaibinhf.project.chemistbe.model.Fee;
import dthaibinhf.project.chemistbe.dto.FeeBasicDto;
import dthaibinhf.project.chemistbe.repository.FeeRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
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

    @Tool(
            name = "Get_all_fees",
            description = "Get all available fee not including payment. " +
                          "Useful for queries like 'what is the free of grade 12' or 'show me all fee structures'")
    @Transactional
    public List<FeeBasicDto> getAllFeesBasic() {
        return feeRepository.findAllActiveFees().stream()
                .map(feeMapper::toBasicDto)
                .collect(Collectors.toList());
    }

    @Tool(
            name = "Get_current_fee_of_group",
            description = "Get the current fee structure for a specific group. " +
                          "Useful for queries like 'what is the current fee for group 1' or 'show me the current fee of group 2'")
    @Transactional
    public FeeBasicDto getCurrentFeeBasicOfGroup(Integer groupId) {
        return feeMapper.toBasicDto(feeRepository.findCurrentFeeOfGroup().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No current fee found")));
    }

    @Tool(
            name = "Get_all_fees_with_details",
            description = "Get all available fee structures and pricing information. " +
                          "Useful for queries like 'what are the fees' or 'show me all fee structures'")
    @Transactional
    public List<FeeDTO> getAllFees() {
        return feeRepository.findAllActiveFees().stream()
                .map(feeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Tool(
            name = "Get_fee_by_id",
            description = "Get detailed fee information for a specific fee by ID. " +
                          "Useful for queries like 'show me fee details for ID 5' or 'what is fee 10'"
    )
    @Transactional
    public FeeDTO getFeeById(@ToolParam(description = "The unique ID of the fee") Integer id) {
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