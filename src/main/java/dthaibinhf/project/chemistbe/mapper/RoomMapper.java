package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.RoomDTO;
import dthaibinhf.project.chemistbe.model.Room;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
@Primary
public interface RoomMapper {
    Room toEntity(RoomDTO roomDTO);

    RoomDTO toDto(Room room);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Room partialUpdate(RoomDTO roomDTO, @MappingTarget Room room);
}