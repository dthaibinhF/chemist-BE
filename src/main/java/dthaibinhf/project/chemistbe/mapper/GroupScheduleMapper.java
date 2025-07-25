package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.GroupScheduleDTO;
import dthaibinhf.project.chemistbe.model.Group;
import dthaibinhf.project.chemistbe.model.GroupSchedule;
import dthaibinhf.project.chemistbe.repository.GroupRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
@Primary
public abstract class GroupScheduleMapper {

    @Autowired
    GroupRepository groupRepository;

    @Mapping(source = "roomName", target = "room.name")
    @Mapping(source = "roomId", target = "room.id")
    @Mapping(target = "group", ignore = true)
    abstract public GroupSchedule toEntity(GroupScheduleDTO groupScheduleDTO);

    @AfterMapping
    protected void linkGroup(@MappingTarget GroupSchedule groupSchedule, GroupScheduleDTO groupScheduleDTO) {
        if (groupScheduleDTO.getGroupId() != null) {
            Group group = groupRepository.findById(groupScheduleDTO.getGroupId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Group not found")
            );
            groupSchedule.setGroup(group);
        }
    }


    @Mapping(source = "room.name", target = "roomName")
    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "group.id", target = "groupId")
    @Mapping(source = "group.name", target = "groupName")
    abstract public GroupScheduleDTO toDto(GroupSchedule groupSchedule);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    abstract public GroupSchedule partialUpdate(GroupScheduleDTO groupScheduleDTO, @MappingTarget GroupSchedule groupSchedule);
}