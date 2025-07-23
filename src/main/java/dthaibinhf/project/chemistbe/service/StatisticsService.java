package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.StatisticsDTO;
import dthaibinhf.project.chemistbe.repository.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticsService {

    StudentRepository studentRepository;
    TeacherRepository teacherRepository;
    GroupRepository groupRepository;
    ScheduleRepository scheduleRepository;
    AttendanceRepository attendanceRepository;

    /**
     * Get comprehensive dashboard statistics
     * 
     * @return StatisticsDTO containing various system metrics
     */
    public StatisticsDTO getDashboardStatistics() {
        try {
            log.info("Generating dashboard statistics");
            
            // Count active students
            Long totalStudents = studentRepository.count();
            Long activeStudents = (long) studentRepository.findAllActive().size();
            
            // Count active teachers
            Long totalTeachers = teacherRepository.count();
            Long activeTeachers = (long) teacherRepository.findAllActiveTeachers().size();
            
            // Count active groups
            Long totalGroups = groupRepository.count();
            Long activeGroups = (long) groupRepository.findAllActiveGroups().size();
            
            // Count schedules
            Long totalSchedules = scheduleRepository.count();
            Long thisWeekSchedules = getThisWeekSchedulesCount();
            
            // Count attendances and calculate attendance rate
            Long totalAttendances = attendanceRepository.count();
            BigDecimal attendanceRate = calculateAttendanceRate();
            
            StatisticsDTO statistics = StatisticsDTO.builder()
                    .totalStudents(totalStudents)
                    .activeStudents(activeStudents)
                    .totalTeachers(totalTeachers)
                    .activeTeachers(activeTeachers)
                    .totalGroups(totalGroups)
                    .activeGroups(activeGroups)
                    .totalSchedules(totalSchedules)
                    .thisWeekSchedules(thisWeekSchedules)
                    .totalAttendances(totalAttendances)
                    .attendanceRatePercentage(attendanceRate)
                    .build();
                    
            log.info("Generated statistics: {} students, {} teachers, {} groups, {}% attendance rate",
                    activeStudents, activeTeachers, activeGroups, attendanceRate);
                    
            return statistics;
            
        } catch (Exception e) {
            log.error("Error generating dashboard statistics", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate statistics");
        }
    }
    
    private Long getThisWeekSchedulesCount() {
        try {
            OffsetDateTime startOfWeek = OffsetDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(
                    OffsetDateTime.now().getDayOfWeek().getValue() - 1);
            OffsetDateTime endOfWeek = startOfWeek.plusDays(7);
            
            LocalDate startDate = startOfWeek.toLocalDate();
            LocalDate endDate = endOfWeek.toLocalDate();
            
            return (long) scheduleRepository.findAllActivePageable(null, startOfWeek, endOfWeek,
                    org.springframework.data.domain.Pageable.unpaged()).getContent().size();
        } catch (Exception e) {
            log.warn("Error calculating this week schedules count", e);
            return 0L;
        }
    }
    
    private BigDecimal calculateAttendanceRate() {
        try {
            Long totalAttendances = (long) attendanceRepository.findAllActive().size();
            if (totalAttendances == 0) {
                return BigDecimal.ZERO;
            }
            
            // Count present attendances (assuming "PRESENT" is the status for present)
            Long presentAttendances = attendanceRepository.findAllActive().stream()
                    .filter(attendance -> "PRESENT".equalsIgnoreCase(attendance.getStatus()))
                    .mapToLong(attendance -> 1L)
                    .sum();
            
            if (totalAttendances == 0) {
                return BigDecimal.ZERO;
            }
            
            return BigDecimal.valueOf(presentAttendances)
                    .divide(BigDecimal.valueOf(totalAttendances), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
                    
        } catch (Exception e) {
            log.warn("Error calculating attendance rate", e);
            return BigDecimal.ZERO;
        }
    }
}