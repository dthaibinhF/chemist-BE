package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.ScheduleDTO;
import dthaibinhf.project.chemistbe.model.Schedule;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {GroupMapper.class, TeacherMapper.class, RoomMapper.class}
)
public interface ScheduleMapper {
    /*
     * ! handle set group in service
     * */
    @Mapping(target = "group", ignore = true)
    Schedule toEntity(ScheduleDTO scheduleDTO);

    @AfterMapping
    default void linkAttendance(@MappingTarget Schedule schedule) {
        schedule.getAttendances().forEach(attendance -> attendance.setSchedule(schedule));
    }

    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "group.name", target = "groupName")
    ScheduleDTO toDto(Schedule schedule);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Schedule partialUpdate(ScheduleDTO scheduleDTO, @MappingTarget Schedule schedule);
}