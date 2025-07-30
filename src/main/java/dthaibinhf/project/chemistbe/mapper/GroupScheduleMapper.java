package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.GroupScheduleDTO;
import dthaibinhf.project.chemistbe.model.Group;
import dthaibinhf.project.chemistbe.model.GroupSchedule;
import dthaibinhf.project.chemistbe.model.Room;
import dthaibinhf.project.chemistbe.repository.GroupRepository;
import dthaibinhf.project.chemistbe.repository.RoomRepository;
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

    @Autowired
    RoomRepository roomRepository;

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
    @Mapping(target = "room", ignore = true) // Ignore room mapping, handle manually
    abstract public GroupSchedule partialUpdate(GroupScheduleDTO groupScheduleDTO, @MappingTarget GroupSchedule groupSchedule);


    @AfterMapping
    protected void linkRoom(@MappingTarget GroupSchedule groupSchedule, GroupScheduleDTO groupScheduleDTO) {
        // Handle room update manually
        if (groupScheduleDTO.getRoomId() != null) {
            Room room = roomRepository.findById(groupScheduleDTO.getRoomId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found: " + groupScheduleDTO.getRoomId())
            );
            groupSchedule.setRoom(room);
        } else {
            // If roomId is explicitly null, set room to null
            groupSchedule.setRoom(null);
        }
        // If roomId is not provided in DTO (undefined), leave existing room unchanged
    }


}