package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.AcademicYearDTO;
import dthaibinhf.project.chemistbe.model.AcademicYear;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
@Primary
public interface AcademicYearMapper {
    AcademicYear toEntity(AcademicYearDTO academicYearDTO);

    AcademicYearDTO toDto(AcademicYear academicYear);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AcademicYear partialUpdate(AcademicYearDTO academicYearDTO, @MappingTarget AcademicYear academicYear);
}