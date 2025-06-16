package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.SchoolDTO;
import dthaibinhf.project.chemistbe.model.School;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SchoolMapper {
    School toEntity(SchoolDTO schoolDTO);

    SchoolDTO toDto(School school);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    School partialUpdate(SchoolDTO schoolDTO, @MappingTarget School school);
}