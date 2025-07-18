package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.TeacherDetailDTO;
import dthaibinhf.project.chemistbe.model.TeacherDetail;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {TeacherMapper.class, SchoolMapper.class, SchoolClassMapper.class})
@Primary
public interface TeacherDetailMapper {

    TeacherDetail toEntity(TeacherDetailDTO teacherDetailDTO);

    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", source = "teacher.account.name")
    TeacherDetailDTO toDto(TeacherDetail teacherDetail);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TeacherDetail partialUpdate(TeacherDetailDTO teacherDetailDTO, @MappingTarget TeacherDetail teacherDetail);
}