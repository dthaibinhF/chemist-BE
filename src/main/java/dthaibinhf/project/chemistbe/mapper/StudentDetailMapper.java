package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.model.StudentDetail;
import dthaibinhf.project.chemistbe.model.StudentDetailDTO;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {SchoolMapper.class,
                SchoolClassMapper.class,
                AcademicYearMapper.class,
                GradeMapper.class}
)
@Primary
public interface StudentDetailMapper {
    @Mapping(target = "student", ignore = true)
    @Mapping(source = "groupId", target = "group.id")
    @Mapping(source = "school", target = "school")
    @Mapping(source = "schoolClass", target = "schoolClass")
    @Mapping(source = "academicYear", target = "academicYear")
    @Mapping(source = "grade", target = "grade")
    StudentDetail toEntity(StudentDetailDTO studentDetailDTO);

    @AfterMapping
    default void linkStudent(@MappingTarget StudentDetail studentDetail, StudentDetailDTO studentDetailDTO) {
        if (studentDetailDTO.getStudentId() != null) {
            if (studentDetail.getStudent() == null) {
                studentDetail.setStudent(new dthaibinhf.project.chemistbe.model.Student());
            }
            studentDetail.getStudent().setId(studentDetailDTO.getStudentId());
        }
    }

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.name")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.name")
    @Mapping(target = "school", source = "school")
    @Mapping(target = "schoolClass", source = "schoolClass")
    @Mapping(target = "academicYear", source = "academicYear")
    @Mapping(target = "grade", source = "grade")
    StudentDetailDTO toDto(StudentDetail studentDetail);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "student", ignore = true)
    StudentDetail partialUpdate(StudentDetailDTO studentDetailDTO, @MappingTarget StudentDetail studentDetail);

    @AfterMapping
    default void linkStudentInPartialUpdate(StudentDetailDTO studentDetailDTO, @MappingTarget StudentDetail studentDetail) {
        if (studentDetailDTO.getStudentId() != null) {
            if (studentDetail.getStudent() == null) {
                studentDetail.setStudent(new dthaibinhf.project.chemistbe.model.Student());
            }
            studentDetail.getStudent().setId(studentDetailDTO.getStudentId());
        }
    }
}
