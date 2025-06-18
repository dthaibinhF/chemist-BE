package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.AccountDTO;
import dthaibinhf.project.chemistbe.dto.request.RegisterRequest;
import dthaibinhf.project.chemistbe.model.Account;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {RoleMapper.class})
public interface AccountMapper {
    Account toAccount(RegisterRequest registerRequest);

    @Mapping(target = "role", ignore = true)
    Account toEntity(AccountDTO accountDTO);


    @Mapping(source = "role.id", target = "roleId")
    @Mapping(source = "role.name", target = "roleName")
    AccountDTO toDto(Account account);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Account partialUpdate(AccountDTO accountDTO, @MappingTarget Account account);
}