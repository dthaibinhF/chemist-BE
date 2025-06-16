package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.StudentDTO;
import dthaibinhf.project.chemistbe.model.Student;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {ScoreMapper.class})
public interface StudentMapper {
    Student toEntity(StudentDTO studentDTO);

    @Mapping(source = "scores", target = "scores")
    StudentDTO toDto(Student student);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Student partialUpdate(StudentDTO studentDTO, @MappingTarget Student student);
}