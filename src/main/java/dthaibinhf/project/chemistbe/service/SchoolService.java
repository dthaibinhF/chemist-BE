package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.SchoolDTO;
import dthaibinhf.project.chemistbe.mapper.SchoolMapper;
import dthaibinhf.project.chemistbe.model.School;
import dthaibinhf.project.chemistbe.repository.SchoolRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class SchoolService {

    SchoolRepository schoolRepository;
    SchoolMapper schoolMapper;


    @Transactional
    public SchoolDTO createSchool(@Valid SchoolDTO schoolDTO) {
        if (schoolRepository.findSchoolByName(schoolDTO.getName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "School name already exists");
        }
        School school = schoolMapper.toEntity(schoolDTO);
        school.setId(null);
        School savedSchool = schoolRepository.save(school);
        return schoolMapper.toDto(savedSchool);
    }

    public List<SchoolDTO> getAllSchools() {
        return schoolRepository.findAllActiveSchools().stream()
                .map(schoolMapper::toDto)
                .collect(Collectors.toList());
    }

    public SchoolDTO getSchoolById(Integer id) {
        School school = schoolRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School not found: " + id));
        return schoolMapper.toDto(school);
    }

    @Transactional
    public SchoolDTO updateSchool(Integer id, @Valid SchoolDTO schoolDTO) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School not found: " + id));
        schoolMapper.partialUpdate(schoolDTO, school);
        School updatedSchool = schoolRepository.save(school);
        return schoolMapper.toDto(updatedSchool);
    }

    @Transactional
    public void deleteSchool(Integer id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "School not found: " + id));
        school.softDelete();
        schoolRepository.save(school);
    }
}
