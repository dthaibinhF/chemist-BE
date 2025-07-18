package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.model.Grade;
import dthaibinhf.project.chemistbe.dto.GradeDTO;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
@Primary
public interface GradeMapper {
    Grade toEntity(GradeDTO gradeDTO);

    GradeDTO toDto(Grade grade);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Grade partialUpdate(GradeDTO gradeDTO, @MappingTarget Grade grade);
}