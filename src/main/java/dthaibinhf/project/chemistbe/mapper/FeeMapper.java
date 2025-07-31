package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.FeeDTO;
import dthaibinhf.project.chemistbe.model.Fee;
import dthaibinhf.project.chemistbe.dto.FeeBasicDto;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {PaymentDetailMapper.class, GroupMapper.class})
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

    FeeBasicDto toBasicDto(Fee fee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Fee partialUpdate(FeeDTO feeDTO, @MappingTarget Fee fee);

    Fee toEntity(FeeBasicDto feeBasicDto);

    @AfterMapping
    default void linkGroups(@MappingTarget Fee fee) {
        fee.getGroups().forEach(group -> group.setFee(fee));
    }

    FeeBasicDto toDto1(Fee fee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Fee partialUpdate(FeeBasicDto feeBasicDto, @MappingTarget Fee fee);
}