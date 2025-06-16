package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.model.Fee;
import dthaibinhf.project.chemistbe.dto.FeeDTO;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface FeeMapper {
    Fee toEntity(FeeDTO feeDTO);

    FeeDTO toDto(Fee fee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Fee partialUpdate(FeeDTO feeDTO, @MappingTarget Fee fee);
}