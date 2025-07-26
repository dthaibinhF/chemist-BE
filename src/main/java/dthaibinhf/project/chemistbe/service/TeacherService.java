package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.TeacherDTO;
import dthaibinhf.project.chemistbe.dto.TeacherMonthlySummaryDTO;
import dthaibinhf.project.chemistbe.mapper.TeacherMapper;
import dthaibinhf.project.chemistbe.mapper.TeacherMonthlySummaryMapper;
import dthaibinhf.project.chemistbe.model.SalaryType;
import dthaibinhf.project.chemistbe.model.Teacher;
import dthaibinhf.project.chemistbe.model.TeacherMonthlySummary;
import dthaibinhf.project.chemistbe.repository.TeacherMonthlySummaryRepository;
import dthaibinhf.project.chemistbe.repository.TeacherRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class TeacherService {

    TeacherRepository teacherRepository;
    TeacherMapper teacherMapper;
    TeacherMonthlySummaryRepository monthlySummaryRepository;
    TeacherMonthlySummaryMapper monthlySummaryMapper;

    @Cacheable(value = "teachers", key = "'allTeachers'")
    public List<TeacherDTO> getAllTeachers() {
        return teacherRepository.findAllActiveTeachers().stream()
                .map(teacherMapper::toDto)
                .collect(Collectors.toList());
    }

    public TeacherDTO getTeacherById(Integer id) {
        Teacher teacher = teacherRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found: " + id));
        return teacherMapper.toDto(teacher);
    }

    @Transactional
    @CacheEvict(value = "teachers", allEntries = true)
    public TeacherDTO createTeacher(@Valid TeacherDTO teacherDTO) {
        Teacher teacher = teacherMapper.toEntity(teacherDTO);
        teacher.setId(null);
        Teacher savedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toDto(savedTeacher);
    }

    @Transactional
    @CacheEvict(value = "teachers", allEntries = true)
    public TeacherDTO updateTeacher(Integer id, @Valid TeacherDTO teacherDTO) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found: " + id));
        teacherMapper.partialUpdate(teacherDTO, teacher);
        Teacher updatedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toDto(updatedTeacher);
    }

    @Transactional
    @CacheEvict(value = "teachers", allEntries = true)
    public void deleteTeacher(Integer id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found: " + id));
        teacher.softDelete();
        teacherRepository.save(teacher);
    }

    /**
     * Search teachers with pagination and sorting
     * Search by teacher name, phone, or email
     *
     * @param teacherName search by teacher full name (contains, case-insensitive)
     * @param phone       search by phone number (contains)
     * @param email       search by email (contains, case-insensitive)
     * @param pageable    pagination and sorting parameters
     * @return page of teachers matching the criteria
     */
    public Page<TeacherDTO> search(Pageable pageable,
            String teacherName,
            String phone,
            String email) {
        try {
            log.info("Searching teachers - page: {}, size: {}, sort: {}",
                    pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

            // Prepare search patterns for LIKE queries (add wildcards)
            String teacherNamePattern = (teacherName != null && !teacherName.isEmpty()) ? "%" + teacherName + "%"
                    : null;
            String phonePattern = (phone != null && !phone.isEmpty()) ? "%" + phone + "%" : null;
            String emailPattern = (email != null && !email.isEmpty()) ? "%" + email + "%" : null;

            // Call repository method
            Page<Teacher> teachersPage = teacherRepository.searchTeachers(
                    teacherNamePattern,
                    phonePattern,
                    emailPattern,
                    pageable);

            log.info("Found {} teachers matching search criteria", teachersPage.getTotalElements());

            // Convert Page<Entity> to Page<DTO>
            return teachersPage.map(teacherMapper::toDto);

        } catch (Exception e) {
            log.error("Error searching teachers with pagination", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to search teachers");
        }
    }

    /**
     * Update teacher salary configuration.
     * 
     * @param teacherId The ID of the teacher
     * @param salaryType The salary calculation type
     * @param baseRate The base rate for calculations
     * @return Updated teacher DTO
     */
    @Transactional
    @CacheEvict(value = "teachers", allEntries = true)
    public TeacherDTO updateSalaryConfiguration(Integer teacherId, SalaryType salaryType, BigDecimal baseRate) {
        log.info("Updating salary configuration for teacher {}: type={}, rate={}", teacherId, salaryType, baseRate);
        
        Teacher teacher = teacherRepository.findActiveById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found: " + teacherId));
        
        // Validate inputs
        if (salaryType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salary type cannot be null");
        }
        if (baseRate != null && baseRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Base rate cannot be negative");
        }
        
        teacher.setSalaryType(salaryType);
        teacher.setBaseRate(baseRate);
        
        Teacher savedTeacher = teacherRepository.save(teacher);
        return teacherMapper.toDto(savedTeacher);
    }

    /**
     * Get salary configuration for a teacher.
     * 
     * @param teacherId The ID of the teacher
     * @return Teacher DTO with salary configuration
     */
    public TeacherDTO getSalaryConfiguration(Integer teacherId) {
        Teacher teacher = teacherRepository.findActiveById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found: " + teacherId));
        return teacherMapper.toDto(teacher);
    }

    /**
     * Get monthly salary summaries for a teacher.
     * 
     * @param teacherId The ID of the teacher
     * @param pageable Pagination information
     * @return Page of monthly summaries
     */
    public Page<TeacherMonthlySummaryDTO> getTeacherSalarySummaries(Integer teacherId, Pageable pageable) {
        // Verify teacher exists
        teacherRepository.findActiveById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found: " + teacherId));
        
        Page<TeacherMonthlySummary> summariesPage = monthlySummaryRepository
                .findByTeacherIdOrderByYearDescMonthDesc(teacherId, pageable);
        
        return summariesPage.map(monthlySummaryMapper::toDto);
    }

    /**
     * Get a specific monthly salary summary for a teacher.
     * 
     * @param teacherId The ID of the teacher
     * @param month The month (1-12)
     * @param year The year
     * @return Monthly summary DTO
     */
    public TeacherMonthlySummaryDTO getTeacherMonthlySummary(Integer teacherId, Integer month, Integer year) {
        TeacherMonthlySummary summary = monthlySummaryRepository
                .findByTeacherIdAndMonthAndYear(teacherId, month, year)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                        "No salary summary found for teacher " + teacherId + " for " + month + "/" + year));
        
        return monthlySummaryMapper.toDto(summary);
    }

    /**
     * Get salary history for a teacher within a date range.
     * 
     * @param teacherId The ID of the teacher
     * @param fromYear Starting year
     * @param fromMonth Starting month
     * @param toYear Ending year
     * @param toMonth Ending month
     * @return List of monthly summaries in the date range
     */
    public List<TeacherMonthlySummaryDTO> getTeacherSalaryHistory(Integer teacherId, 
                                                                 Integer fromYear, Integer fromMonth,
                                                                 Integer toYear, Integer toMonth) {
        // Verify teacher exists
        teacherRepository.findActiveById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found: " + teacherId));
        
        List<TeacherMonthlySummary> summaries = monthlySummaryRepository
                .findTeacherSalaryInDateRange(teacherId, fromYear, fromMonth, toYear, toMonth);
        
        return summaries.stream()
                .map(monthlySummaryMapper::toDto)
                .collect(Collectors.toList());
    }
}