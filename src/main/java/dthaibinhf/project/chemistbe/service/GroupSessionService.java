package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.GroupSessionDTO;
import dthaibinhf.project.chemistbe.mapper.GroupSessionMapper;
import dthaibinhf.project.chemistbe.model.Group;
import dthaibinhf.project.chemistbe.model.GroupSession;
import dthaibinhf.project.chemistbe.repository.GroupRepository;
import dthaibinhf.project.chemistbe.repository.GroupSessionRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupSessionService {
    
    GroupSessionRepository groupSessionRepository;
    GroupSessionMapper groupSessionMapper;
    GroupRepository groupRepository;

    public List<GroupSessionDTO> getAllGroupSessions() {
        return groupSessionRepository.findAllActive().stream()
                .map(groupSessionMapper::toDto)
                .collect(Collectors.toList());
    }

    public GroupSessionDTO getGroupSessionById(Integer id) {
        GroupSession groupSession = groupSessionRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GroupSession not found: " + id));
        return groupSessionMapper.toDto(groupSession);
    }

    @Transactional
    public GroupSessionDTO createGroupSession(GroupSessionDTO groupSessionDTO) {
        validateGroupSession(groupSessionDTO);
        
        GroupSession groupSession = groupSessionMapper.toEntity(groupSessionDTO);
        groupSession.setId(null);
        
        // Set the related groups if provided
        if (groupSessionDTO.getGroupIds() != null && !groupSessionDTO.getGroupIds().isEmpty()) {
            Set<Group> groups = groupSessionDTO.getGroupIds().stream()
                    .map(groupId -> groupRepository.findActiveById(groupId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + groupId)))
                    .collect(Collectors.toSet());
            groupSession.setGroups(groups);
        }
        
        GroupSession saved = groupSessionRepository.save(groupSession);
        return groupSessionMapper.toDto(saved);
    }

    @Transactional
    public GroupSessionDTO updateGroupSession(Integer id, GroupSessionDTO groupSessionDTO) {
        GroupSession groupSession = groupSessionRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GroupSession not found: " + id));
        
        validateGroupSession(groupSessionDTO);
        
        // Update basic group session information
        groupSessionMapper.partialUpdate(groupSessionDTO, groupSession);
        
        // Update the related groups if provided
        if (groupSessionDTO.getGroupIds() != null) {
            Set<Group> groups = groupSessionDTO.getGroupIds().stream()
                    .map(groupId -> groupRepository.findActiveById(groupId)
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + groupId)))
                    .collect(Collectors.toSet());
            groupSession.setGroups(groups);
        }
        
        GroupSession updated = groupSessionRepository.save(groupSession);
        return groupSessionMapper.toDto(updated);
    }

    @Transactional
    public void deleteGroupSession(Integer id) {
        GroupSession groupSession = groupSessionRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "GroupSession not found: " + id));
        groupSession.softDelete();
        groupSessionRepository.save(groupSession);
    }

    private void validateGroupSession(GroupSessionDTO groupSessionDTO) {
        if (groupSessionDTO.getStartTime() != null && groupSessionDTO.getEndTime() != null) {
            if (groupSessionDTO.getStartTime().isAfter(groupSessionDTO.getEndTime())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time must be before end time");
            }
        }
        
        if (groupSessionDTO.getSessionType() == null || groupSessionDTO.getSessionType().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session type is required");
        }
        
        if (groupSessionDTO.getDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date is required");
        }
    }
}