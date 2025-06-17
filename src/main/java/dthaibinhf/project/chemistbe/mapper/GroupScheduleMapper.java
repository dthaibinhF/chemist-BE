package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.model.GroupSchedule;
import dthaibinhf.project.chemistbe.dto.GroupScheduleDTO;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface GroupScheduleMapper {
    /*
     * ! handle set group in service
     * */
    @Mapping(target = "group", ignore = true)
    GroupSchedule toEntity(GroupScheduleDTO groupScheduleDTO);

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.name")
    GroupScheduleDTO toDto(GroupSchedule groupSchedule);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    GroupSchedule partialUpdate(GroupScheduleDTO groupScheduleDTO, @MappingTarget GroupSchedule groupSchedule);
}