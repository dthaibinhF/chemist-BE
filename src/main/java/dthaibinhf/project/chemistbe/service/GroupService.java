package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.GroupDTO;
import dthaibinhf.project.chemistbe.dto.GroupListDTO;
import dthaibinhf.project.chemistbe.mapper.GroupMapper;
import dthaibinhf.project.chemistbe.model.Group;
import dthaibinhf.project.chemistbe.model.GroupSchedule;
import dthaibinhf.project.chemistbe.repository.GroupRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class GroupService {

    GroupRepository groupRepository;
    GroupMapper groupMapper;
    GroupScheduleService groupScheduleService;

    public List<GroupListDTO> getAllGroups() {
        return groupRepository.findAllActiveGroups().stream().map(groupMapper::toListDto).collect(Collectors.toList());
    }

    public GroupDTO getGroupById(Integer id) {
        Group group = groupRepository.findActiveById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + id)
        );
        return groupMapper.toDto(group);
    }

    public List<GroupListDTO> getGroupsByAcademicYearId(Integer academicYearId) {
        return groupRepository.findActiveByAcademicYearId(academicYearId)
                .stream().map(groupMapper::toListDto).collect(Collectors.toList());
    }

    public List<GroupListDTO> getGroupsByGradeId(Integer gradeId) {
        return groupRepository.findActiveByGradeId(gradeId).stream()
                .map(groupMapper::toListDto)
                .collect(Collectors.toList());
    }

    public List<GroupDTO> getAllGroupsWithDetail() {
        return groupRepository.findAllActiveGroups().stream().map(groupMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public GroupDTO createGroup(@Valid GroupDTO groupDTO) {
        Group group = groupMapper.toEntity(groupDTO);
        group.setId(null);
        Group savedGroup = groupRepository.save(group);
        return groupMapper.toDto(savedGroup);
    }

    @Transactional
    public GroupDTO updateGroup(Integer id, @Valid GroupDTO groupDTO) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + id));

        // Store original group schedules for comparison
        Set<GroupSchedule> originalGroupSchedules = new HashSet<>(group.getGroupSchedules());

        // Update the group
        groupMapper.partialUpdate(groupDTO, group);
        Group updatedGroup = groupRepository.save(group);

        // If group schedules were updated, synchronize with schedules
        if (groupDTO.getGroupSchedules() != null) {
            synchronizeSchedulesWithGroupSchedules(originalGroupSchedules, updatedGroup.getGroupSchedules());
        }

        return groupMapper.toDto(updatedGroup);
    }

    private void synchronizeSchedulesWithGroupSchedules(Set<GroupSchedule> originalSchedules, Set<GroupSchedule> updatedSchedules) {
        // For each updated group schedule, find the corresponding original schedule and update related schedules
        for (GroupSchedule updatedSchedule : updatedSchedules) {
            // Find matching original schedule by ID
            Optional<GroupSchedule> originalScheduleOpt = originalSchedules.stream()
                    .filter(s -> s.getId() != null && s.getId().equals(updatedSchedule.getId()))
                    .findFirst();

            if (originalScheduleOpt.isPresent()) {
                GroupSchedule originalSchedule = originalScheduleOpt.get();
                // Only update if there are changes
                if (!originalSchedule.getDayOfWeek().equals(updatedSchedule.getDayOfWeek()) ||
                    !originalSchedule.getStartTime().equals(updatedSchedule.getStartTime()) ||
                    !originalSchedule.getEndTime().equals(updatedSchedule.getEndTime()) ||
                    (originalSchedule.getRoom() == null && updatedSchedule.getRoom() != null) ||
                    (originalSchedule.getRoom() != null && updatedSchedule.getRoom() != null && 
                     !originalSchedule.getRoom().getId().equals(updatedSchedule.getRoom().getId()))) {

                    // Call the method from GroupScheduleService to update related schedules
                    groupScheduleService.updateRelatedSchedules(updatedSchedule, originalSchedule.getDayOfWeekEnum());
                }
            }
        }
    }

    @Transactional
    public void deleteGroup(Integer id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + id));
        group.softDelete();
        groupRepository.save(group);
    }
}
