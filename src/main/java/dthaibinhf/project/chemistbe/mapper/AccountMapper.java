package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.AccountDTO;
import dthaibinhf.project.chemistbe.dto.request.RegisterRequest;
import dthaibinhf.project.chemistbe.model.Account;
import dthaibinhf.project.chemistbe.model.Role;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {RoleMapper.class})
@Primary
public interface AccountMapper {
    Account toAccount(RegisterRequest registerRequest);

    @Mapping(target = "roles", ignore = true)
    Account toEntity(AccountDTO accountDTO);

    @Mapping(source = "roles", target = "roleIds", qualifiedByName = "rolesToRoleIds")
    @Mapping(source = "roles", target = "roleNames", qualifiedByName = "rolesToRoleNames")
    @Mapping(source = "roles", target = "primaryRoleId", qualifiedByName = "rolesToPrimaryRoleId")
    @Mapping(source = "roles", target = "primaryRoleName", qualifiedByName = "rolesToPrimaryRoleName")
    AccountDTO toDto(Account account);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roles", ignore = true)
    Account partialUpdate(AccountDTO accountDTO, @MappingTarget Account account);

    // Custom mapping methods for role collections
    @Named("rolesToRoleIds")
    default List<Integer> rolesToRoleIds(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        return roles.stream()
                .map(Role::getId)
                .collect(Collectors.toList());
    }

    @Named("rolesToRoleNames")
    default List<String> rolesToRoleNames(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }

    @Named("rolesToPrimaryRoleId")
    default Integer rolesToPrimaryRoleId(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        Role primaryRole = roles.iterator().next();
        return primaryRole.getId();
    }

    @Named("rolesToPrimaryRoleName")
    default String rolesToPrimaryRoleName(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        Role primaryRole = roles.iterator().next();
        return primaryRole.getName();
    }
}