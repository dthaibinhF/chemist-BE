package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.model.StudentDetail;
import dthaibinhf.project.chemistbe.model.StudentDetailDTO;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {SchoolMapper.class,
                SchoolClassMapper.class,
                AcademicYearMapper.class,
                GradeMapper.class,
                StudentMapper.class}
)
@Primary
public interface StudentDetailMapper {
    @Mapping(target = "group", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "school", ignore = true)
    @Mapping(target = "schoolClass", ignore = true)
    @Mapping(target = "academicYear", ignore = true)
    @Mapping(target = "grade", ignore = true)
    StudentDetail toEntity(StudentDetailDTO studentDetailDTO);

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.name")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    StudentDetailDTO toDto(StudentDetail studentDetail);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    StudentDetail partialUpdate(StudentDetailDTO studentDetailDTO, @MappingTarget StudentDetail studentDetail);
}