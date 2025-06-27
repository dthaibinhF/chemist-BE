package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.model.SchoolClass;
import dthaibinhf.project.chemistbe.dto.SchoolClassDTO;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
@Primary
public interface SchoolClassMapper {
    SchoolClass toEntity(SchoolClassDTO schoolClassDTO);

    SchoolClassDTO toDto(SchoolClass schoolClass);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    SchoolClass partialUpdate(SchoolClassDTO schoolClassDTO, @MappingTarget SchoolClass schoolClass);
}