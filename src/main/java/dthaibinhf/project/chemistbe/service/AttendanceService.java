package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.AttendanceDTO;
import dthaibinhf.project.chemistbe.mapper.AttendanceMapper;
import dthaibinhf.project.chemistbe.model.Attendance;
import dthaibinhf.project.chemistbe.repository.AttendanceRepository;
import dthaibinhf.project.chemistbe.repository.ScheduleRepository;
import dthaibinhf.project.chemistbe.repository.StudentRepository;
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
}
