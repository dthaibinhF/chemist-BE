package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.PaymentDetailDTO;
import dthaibinhf.project.chemistbe.model.Fee;
import dthaibinhf.project.chemistbe.model.Student;
import dthaibinhf.project.chemistbe.model.PaymentDetail;
import dthaibinhf.project.chemistbe.repository.FeeRepository;
import dthaibinhf.project.chemistbe.repository.StudentRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {FeeMapper.class, StudentMapper.class}
)
@Primary
public abstract class PaymentDetailMapper {
    @Autowired
    protected FeeRepository feeRepository;

    @Autowired
    protected StudentRepository studentRepository;

    @Mapping(target = "fee", ignore = true)
    @Mapping(target = "student", ignore = true)
    public abstract PaymentDetail toEntity(PaymentDetailDTO paymentDetailDTO);

    @AfterMapping
    protected void linkFeeAndStudent(@MappingTarget PaymentDetail paymentDetail, PaymentDetailDTO paymentDetailDTO) {
        Fee fee = feeRepository.findById(paymentDetailDTO.getFeeId())
                .orElseThrow(() -> new IllegalArgumentException("Fee not found: " + paymentDetailDTO.getFeeId()));
        paymentDetail.setFee(fee);
        Student student = studentRepository.findById(paymentDetailDTO.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + paymentDetailDTO.getStudentId()));
        paymentDetail.setStudent(student);
    }

    @Mapping(source = "fee.id", target = "feeId")
    @Mapping(source = "fee.name", target = "feeName")
    @Mapping(source = "student.id", target = "studentId")
    @Mapping(source = "student.name", target = "studentName")
    @Mapping(source = "paymentStatus", target = "paymentStatus")
    @Mapping(source = "dueDate", target = "dueDate")
    @Mapping(source = "generatedAmount", target = "generatedAmount")
    @Mapping(target = "isOverdue", expression = "java(paymentDetail.isOverdue())")
    public abstract PaymentDetailDTO toDto(PaymentDetail paymentDetail);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "fee", ignore = true)
    @Mapping(target = "student", ignore = true)
    public abstract PaymentDetail partialUpdate(PaymentDetailDTO paymentDetailDTO, @MappingTarget PaymentDetail paymentDetail);
}