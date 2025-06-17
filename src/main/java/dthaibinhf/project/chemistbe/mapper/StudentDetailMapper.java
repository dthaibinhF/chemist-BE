package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.model.StudentDetail;
import dthaibinhf.project.chemistbe.model.StudentDetailDTO;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {SchoolMapper.class,
                SchoolClassMapper.class,
                AcademicYearMapper.class,
                GradeMapper.class,
                StudentMapper.class}
)
public interface StudentDetailMapper {
    /*
     * ! handle set group in service
     * */
    @Mapping(target = "group", ignore = true)
    StudentDetail toEntity(StudentDetailDTO studentDetailDTO);

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "groupName", source = "group.name")
    StudentDetailDTO toDto(StudentDetail studentDetail);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    StudentDetail partialUpdate(StudentDetailDTO studentDetailDTO, @MappingTarget StudentDetail studentDetail);
}