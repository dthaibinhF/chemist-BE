package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.model.Attendance;
import dthaibinhf.project.chemistbe.dto.AttendanceDTO;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ScheduleMapper.class, StudentMapper.class})
@Primary
public interface AttendanceMapper {
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    Attendance toEntity(AttendanceDTO attendanceDTO);

    @Mapping(source = "schedule.id", target = "scheduleId")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.name", target = "studentName")
    @Mapping(source = "schedule.group.id", target = "groupId")
    @Mapping(source = "schedule.group.name", target = "groupName")
    AttendanceDTO toDto(Attendance attendance);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Attendance partialUpdate(AttendanceDTO attendanceDTO, @MappingTarget Attendance attendance);
}