package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.TeacherDTO;
import dthaibinhf.project.chemistbe.model.Teacher;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {AccountMapper.class})
@Primary
public interface TeacherMapper {
    Teacher toEntity(TeacherDTO teacherDTO);

    @AfterMapping
    default void linkTeacherDetail(@MappingTarget Teacher teacher) {
        teacher.getTeacherDetails().forEach(teacherDetail -> teacherDetail.setTeacher(teacher));
    }

    @AfterMapping
    default void linkSchedules(@MappingTarget Teacher teacher) {
        if (teacher.getSchedules() != null) {
            teacher.getSchedules().forEach(schedule -> schedule.setTeacher(teacher));
        }
    }

    TeacherDTO toDto(Teacher teacher);



    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Teacher partialUpdate(TeacherDTO teacherDTO, @MappingTarget Teacher teacher);
}