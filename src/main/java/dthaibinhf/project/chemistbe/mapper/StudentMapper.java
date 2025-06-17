package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.StudentDTO;
import dthaibinhf.project.chemistbe.model.Student;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {ScoreMapper.class, PaymentDetailMapper.class})
public interface StudentMapper {
    @Mapping(target = "paymentDetails", ignore = true)
    Student toEntity(StudentDTO studentDTO);

    @AfterMapping
    default void linkAttendance(@MappingTarget Student student) {
        student.getAttendances().forEach(attendance -> attendance.setStudent(student));
    }

    @AfterMapping
    default void linkPaymentDetail(@MappingTarget Student student) {
        student.getPaymentDetails().forEach(paymentDetail -> paymentDetail.setStudent(student));
    }

    @Mapping(source = "scores", target = "scores")
    StudentDTO toDto(Student student);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Student partialUpdate(StudentDTO studentDTO, @MappingTarget Student student);
}