package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.TeacherDTO;
import dthaibinhf.project.chemistbe.model.Teacher;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {AccountMapper.class})
public interface TeacherMapper {
    Teacher toEntity(TeacherDTO teacherDTO);

    TeacherDTO toDto(Teacher teacher);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Teacher partialUpdate(TeacherDTO teacherDTO, @MappingTarget Teacher teacher);
}