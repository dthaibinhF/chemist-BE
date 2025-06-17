package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.AccountDTO;
import dthaibinhf.project.chemistbe.dto.request.RegisterRequest;
import dthaibinhf.project.chemistbe.dto.response.RegisterResponse;
import dthaibinhf.project.chemistbe.model.Account;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {RoleMapper.class})
public interface AccountMapper {
    Account toAccount(RegisterRequest registerRequest);

    RegisterResponse toRegisterResponse(Account account);

    AccountDTO toDTO(Account account);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Account partialUpdate(AccountDTO accountDTO, @MappingTarget Account account);

    Account toEntity(AccountDTO accountDTO);
}