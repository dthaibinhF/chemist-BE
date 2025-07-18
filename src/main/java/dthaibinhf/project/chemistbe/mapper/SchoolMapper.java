package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.SchoolDTO;
import dthaibinhf.project.chemistbe.model.School;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
@Primary
public interface SchoolMapper {
    School toEntity(SchoolDTO schoolDTO);

    SchoolDTO toDto(School school);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    School partialUpdate(SchoolDTO schoolDTO, @MappingTarget School school);
}