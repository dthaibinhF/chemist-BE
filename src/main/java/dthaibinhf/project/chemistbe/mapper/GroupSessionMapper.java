package dthaibinhf.project.chemistbe.mapper;

import dthaibinhf.project.chemistbe.dto.GroupSessionDTO;
import dthaibinhf.project.chemistbe.model.Group;
import dthaibinhf.project.chemistbe.model.GroupSession;
import org.mapstruct.*;
import org.springframework.context.annotation.Primary;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
@Primary
public interface GroupSessionMapper {
    @Mapping(target = "groups", ignore = true)
    GroupSession toEntity(GroupSessionDTO groupSessionDTO);

    @Mapping(source = "groups", target = "groupIds", qualifiedByName = "groupsToGroupIds")
    GroupSessionDTO toDto(GroupSession groupSession);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    GroupSession partialUpdate(GroupSessionDTO groupSessionDTO, @MappingTarget GroupSession groupSession);

    @Named("groupsToGroupIds")
    default Set<Integer> groupsToGroupIds(Set<Group> groups) {
        return groups != null ? groups.stream()
                .map(Group::getId)
                .collect(Collectors.toSet()) : null;
    }
}