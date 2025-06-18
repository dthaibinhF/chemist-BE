package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.AcademicYearDTO;
import dthaibinhf.project.chemistbe.mapper.AcademicYearMapper;
import dthaibinhf.project.chemistbe.model.AcademicYear;
import dthaibinhf.project.chemistbe.repository.AcademicYearRepository;
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
public class AcademicYearService {

    AcademicYearRepository academicYearRepository;
    AcademicYearMapper academicYearMapper;

    public List<AcademicYearDTO> getAllAcademicYears() {
        return academicYearRepository.findAllActiveAcademicYears().stream()
                .map(academicYearMapper::toDto)
                .collect(Collectors.toList());
    }

    public AcademicYearDTO getAcademicYearById(Integer id) {
        AcademicYear academicYear = academicYearRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic Year not found: " + id));
        return academicYearMapper.toDto(academicYear);
    }

    @Transactional
    public AcademicYearDTO createAcademicYear(@Valid AcademicYearDTO academicYearDTO) {
        AcademicYear academicYear = academicYearMapper.toEntity(academicYearDTO);
        academicYear.setId(null);
        AcademicYear savedAcademicYear = academicYearRepository.save(academicYear);
        return academicYearMapper.toDto(savedAcademicYear);
    }

    @Transactional
    public AcademicYearDTO updateAcademicYear(Integer id, @Valid AcademicYearDTO academicYearDTO) {
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic Year not found: " + id));
        academicYearMapper.partialUpdate(academicYearDTO, academicYear);
        AcademicYear updatedAcademicYear = academicYearRepository.save(academicYear);
        return academicYearMapper.toDto(updatedAcademicYear);
    }

    @Transactional
    @CacheEvict(value = {"academicYears", "allAcademicYears"}, key = "#id")
    public void deleteAcademicYear(Integer id) {
        AcademicYear academicYear = academicYearRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic Year not found: " + id));
        academicYear.softDelete();
        academicYearRepository.save(academicYear);
    }
}