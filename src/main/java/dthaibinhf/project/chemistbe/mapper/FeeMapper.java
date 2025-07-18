package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.FeeDTO;
import dthaibinhf.project.chemistbe.model.Fee;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {PaymentDetailMapper.class})
@Primary
public interface FeeMapper {
    @Mapping(target = "paymentDetails", ignore = true)
    @Mapping(target = "groups", ignore = true)
    Fee toEntity(FeeDTO feeDTO);

    @AfterMapping
    default void linkPaymentDetail(@MappingTarget Fee fee) {
        fee.getPaymentDetails().forEach(paymentDetail -> paymentDetail.setFee(fee));
    }

    FeeDTO toDto(Fee fee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Fee partialUpdate(FeeDTO feeDTO, @MappingTarget Fee fee);
}