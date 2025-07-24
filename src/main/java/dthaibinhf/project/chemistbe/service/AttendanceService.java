package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.AttendanceDTO;
import dthaibinhf.project.chemistbe.dto.BulkAttendanceDTO;
import dthaibinhf.project.chemistbe.mapper.AttendanceMapper;
import dthaibinhf.project.chemistbe.model.Attendance;
import dthaibinhf.project.chemistbe.model.Schedule;
import dthaibinhf.project.chemistbe.model.Student;
import dthaibinhf.project.chemistbe.repository.AttendanceRepository;
import dthaibinhf.project.chemistbe.repository.ScheduleRepository;
import dthaibinhf.project.chemistbe.repository.StudentRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceService {
    AttendanceRepository attendanceRepository;
    AttendanceMapper attendanceMapper;
    ScheduleRepository scheduleRepository;
    StudentRepository studentRepository;

    public List<AttendanceDTO> getAllAttendances() {
        return attendanceRepository.findAllActive().stream()
                .map(attendanceMapper::toDto)
                .collect(Collectors.toList());
    }

    public AttendanceDTO getAttendanceById(Integer id) {
        Attendance attendance = attendanceRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found: " + id));
        return attendanceMapper.toDto(attendance);
    }

    @Transactional
    public AttendanceDTO createAttendance(AttendanceDTO attendanceDTO) {
        Attendance attendance = attendanceMapper.toEntity(attendanceDTO);
        attendance.setId(null);
        setRelatedEntities(attendance, attendanceDTO);
        Attendance saved = attendanceRepository.save(attendance);
        return attendanceMapper.toDto(saved);
    }

    @Transactional
    public AttendanceDTO updateAttendance(Integer id, AttendanceDTO attendanceDTO) {
        Attendance attendance = attendanceRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found: " + id));
        attendanceMapper.partialUpdate(attendanceDTO, attendance);
        setRelatedEntities(attendance, attendanceDTO);
        Attendance updated = attendanceRepository.save(attendance);
        return attendanceMapper.toDto(updated);
    }

    @Transactional
    public void deleteAttendance(Integer id) {
        Attendance attendance = attendanceRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance not found: " + id));
        attendance.softDelete();
        attendanceRepository.save(attendance);
    }

    private void setRelatedEntities(Attendance attendance, AttendanceDTO attendanceDTO) {
        attendance.setSchedule(scheduleRepository.findById(attendanceDTO.getScheduleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found: " + attendanceDTO.getScheduleId())));
        attendance.setStudent(studentRepository.findById(attendanceDTO.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + attendanceDTO.getStudentId())));
    }

    public List<AttendanceDTO> searchAttendanceByGroupAndSchedule(Integer groupId, Integer scheduleId) {
        return attendanceRepository.findByGroupIdAndScheduleId(groupId, scheduleId).stream()
                .map(attendanceMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Create multiple attendance records for a schedule
     * 
     * @param bulkAttendanceDTO bulk attendance data containing schedule ID and list of attendance records
     * @return list of created attendance DTOs
     */
    @Transactional
    public List<AttendanceDTO> createBulkAttendance(BulkAttendanceDTO bulkAttendanceDTO) {
        try {
            log.info("Creating bulk attendance for schedule: {} with {} records", 
                    bulkAttendanceDTO.getScheduleId(), bulkAttendanceDTO.getAttendanceRecords().size());
            
            // Validate schedule exists
            Schedule schedule = scheduleRepository.findActiveById(bulkAttendanceDTO.getScheduleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                            "Schedule not found: " + bulkAttendanceDTO.getScheduleId()));

            List<Attendance> attendanceList = new ArrayList<>();
            
            for (BulkAttendanceDTO.AttendanceRecordDTO recordDTO : bulkAttendanceDTO.getAttendanceRecords()) {
                // Validate student exists
                Student student = studentRepository.findActiveById(recordDTO.getStudentId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                                "Student not found: " + recordDTO.getStudentId()));
                
                // Check if attendance already exists for this schedule and student
                boolean existingAttendance = attendanceRepository.existsByScheduleIdAndStudentIdAndDeletedFalse(
                        schedule.getId(), student.getId());
                
                if (!existingAttendance) {
                    Attendance attendance = new Attendance();
                    attendance.setSchedule(schedule);
                    attendance.setStudent(student);
                    attendance.setStatus(recordDTO.getStatus());
                    attendance.setDescription(recordDTO.getDescription());
                    attendanceList.add(attendance);
                } else {
                    log.warn("Attendance already exists for schedule {} and student {}", 
                            schedule.getId(), student.getId());
                }
            }

            if (!attendanceList.isEmpty()) {
                List<Attendance> savedAttendances = attendanceRepository.saveAll(attendanceList);
                log.info("Successfully created {} attendance records", savedAttendances.size());
                
                return savedAttendances.stream()
                        .map(attendanceMapper::toDto)
                        .collect(Collectors.toList());
            } else {
                log.info("No new attendance records created - all already exist");
                return new ArrayList<>();
            }
            
        } catch (ResponseStatusException e) {
            log.error("Validation error creating bulk attendance", e);
            throw e;
        } catch (Exception e) {
            log.error("Error creating bulk attendance", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create bulk attendance");
        }
    }

    /**
     * Update multiple attendance records for a schedule
     * 
     * @param bulkAttendanceDTO bulk attendance data containing schedule ID and list of attendance records
     * @return list of updated attendance DTOs
     */
    @Transactional
    public List<AttendanceDTO> updateBulkAttendance(BulkAttendanceDTO bulkAttendanceDTO) {
        try {
            log.info("Updating bulk attendance for schedule: {} with {} records", 
                    bulkAttendanceDTO.getScheduleId(), bulkAttendanceDTO.getAttendanceRecords().size());
            
            // Validate schedule exists
            Schedule schedule = scheduleRepository.findActiveById(bulkAttendanceDTO.getScheduleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                            "Schedule not found: " + bulkAttendanceDTO.getScheduleId()));

            List<Attendance> updatedAttendances = new ArrayList<>();
            
            for (BulkAttendanceDTO.AttendanceRecordDTO recordDTO : bulkAttendanceDTO.getAttendanceRecords()) {
                // Find existing attendance record
                List<Attendance> existingAttendances = attendanceRepository
                        .findByScheduleIdAndStudentIdAndDeletedFalse(schedule.getId(), recordDTO.getStudentId());
                
                if (!existingAttendances.isEmpty()) {
                    Attendance attendance = existingAttendances.get(0); // Get the first (should be only one)
                    attendance.setStatus(recordDTO.getStatus());
                    attendance.setDescription(recordDTO.getDescription());
                    updatedAttendances.add(attendance);
                } else {
                    log.warn("No existing attendance found for schedule {} and student {}", 
                            schedule.getId(), recordDTO.getStudentId());
                }
            }

            if (!updatedAttendances.isEmpty()) {
                List<Attendance> savedAttendances = attendanceRepository.saveAll(updatedAttendances);
                log.info("Successfully updated {} attendance records", savedAttendances.size());
                
                return savedAttendances.stream()
                        .map(attendanceMapper::toDto)
                        .collect(Collectors.toList());
            } else {
                log.info("No attendance records updated");
                return new ArrayList<>();
            }
            
        } catch (ResponseStatusException e) {
            log.error("Validation error updating bulk attendance", e);
            throw e;
        } catch (Exception e) {
            log.error("Error updating bulk attendance", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update bulk attendance");
        }
    }
}
