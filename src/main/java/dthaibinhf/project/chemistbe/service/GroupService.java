package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.GroupDTO;
import dthaibinhf.project.chemistbe.dto.GroupListDTO;
import dthaibinhf.project.chemistbe.dto.GroupScheduleDTO;
import dthaibinhf.project.chemistbe.mapper.GroupMapper;
import dthaibinhf.project.chemistbe.mapper.GroupScheduleMapper;
import dthaibinhf.project.chemistbe.model.Group;
import dthaibinhf.project.chemistbe.model.GroupSchedule;
import dthaibinhf.project.chemistbe.repository.GroupRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
@Slf4j
public class GroupService {

    GroupRepository groupRepository;
    GroupMapper groupMapper;
    GroupScheduleService groupScheduleService;
    GroupScheduleMapper groupScheduleMapper;

    @Tool(
            name = "Get_all_groups",
            description = "Get all available groups/classes in the system with small and enough information for Fee, group Schedule. " +
                        "Useful for queries like 'show me all groups' or 'list all classes'")
    @Transactional
    @Cacheable("groups")
    @CacheEvict(value = "groups", allEntries = true) // Add this annotation
    public List<GroupListDTO> getAllGroups() {
        return groupRepository.findAllActiveGroups().stream().map(groupMapper::toListDto).collect(Collectors.toList());
    }

    @Tool(
            name = "Get_group_by_id",
            description = "Get detailed information about a specific group/class by ID. Useful for queries like 'show me group 5' or 'details of class ID 10'")
    @Transactional
    @Cacheable(value = "groups", key = "#id")
    public GroupDTO getGroupById(@ToolParam(description = "The unique ID of the group or class") Integer id) {
        Group group = groupRepository.findActiveById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + id)
        );
        return groupMapper.toDto(group);
    }

    @Cacheable(value = "groups", key = "'academic_year_' + #academicYearId")
    public List<GroupListDTO> getGroupsByAcademicYearId(Integer academicYearId) {
        return groupRepository.findActiveByAcademicYearId(academicYearId)
                .stream().map(groupMapper::toListDto).collect(Collectors.toList());
    }

    @Tool(
            name = "Get_groups_by_grade",
            description = "Get all groups/classes available for a specific grade level. Useful for queries like 'how many groups in Grade 10' or 'show classes for grade 9'"
    )
    @Transactional
    @Cacheable(value = "groups", key = "'grade_' + #gradeId")
    public List<GroupListDTO> getGroupsByGradeId(@ToolParam(description = "The grade level (e.g., 9, 10, 11, 12)") Integer gradeId) {
        return groupRepository.findActiveByGradeId(gradeId).stream()
                .map(groupMapper::toListDto)
                .collect(Collectors.toList());
    }

    @Tool(
            name = "Get_all_groups_with_detail",
            description = "Get detailed information about all groups/classes including schedules and teachers, and student in group. available groups/classes in the system with all details so the data maybe very large, capable for system or asking deep about group " +
                          "Useful for ADMIN, MANAGER, TEACHER for comprehensive group information queries like 'show all groups with details' or 'list classes with schedules' 'how many student in group'")
    @Transactional
    @Cacheable(value = "groups", key = "'with_detail'")
    public List<GroupDTO> getAllGroupsWithDetail() {
        return groupRepository.findAllActiveGroups().stream().map(groupMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "groups", allEntries = true)
    public GroupDTO createGroup(@Valid GroupDTO groupDTO) {
        Group group = groupMapper.toEntity(groupDTO);
        group.setId(null);
        Group savedGroup = groupRepository.save(group);
        return groupMapper.toDto(savedGroup);
    }

    @Transactional
    @CacheEvict(value = "groups", allEntries = true)
    public GroupDTO updateGroup(Integer id, @Valid GroupDTO groupDTO, boolean syncFutureSchedules) {
        Group group = groupRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + id));

        // Store original group schedules for comparison - create deep copies to preserve original values
        Set<GroupSchedule> originalGroupSchedules = group.getGroupSchedules().stream()
                .map(this::createGroupScheduleCopy)
                .collect(Collectors.toSet());

        // Update the group (GroupSchedules are handled separately to preserve IDs)
        groupMapper.partialUpdate(groupDTO, group);
        
        // Handle GroupSchedule updates manually to preserve entity IDs
        if (groupDTO.getGroupSchedules() != null) {
            updateGroupSchedulesManually(group, groupDTO.getGroupSchedules());
        }
        
        Group updatedGroup = groupRepository.save(group);

        // If group schedules were updated and sync is enabled, synchronize with schedules
        if (groupDTO.getGroupSchedules() != null && syncFutureSchedules) {
            log.info("Starting GroupSchedule cascade sync for Group ID: {} with {} original schedules and {} updated schedules",
                    id, originalGroupSchedules.size(), updatedGroup.getGroupSchedules().size());
            synchronizeSchedulesWithGroupSchedules(originalGroupSchedules, updatedGroup.getGroupSchedules());
        } else {
            log.info("Skipping GroupSchedule cascade sync for Group ID: {} - groupSchedules: {}, syncEnabled: {}",
                    id, groupDTO.getGroupSchedules() != null, syncFutureSchedules);
        }

        return groupMapper.toDto(updatedGroup);
    }

    // Overloaded method for backward compatibility
    public GroupDTO updateGroup(Integer id, @Valid GroupDTO groupDTO) {
        return updateGroup(id, groupDTO, true);
    }

    private void synchronizeSchedulesWithGroupSchedules(Set<GroupSchedule> originalSchedules, Set<GroupSchedule> updatedSchedules) {
        log.info("Synchronizing schedules - comparing {} original with {} updated GroupSchedules",
                originalSchedules.size(), updatedSchedules.size());

        // For each updated group schedule, find the corresponding original schedule and update related schedules
        for (GroupSchedule updatedSchedule : updatedSchedules) {
            log.info("Processing updated GroupSchedule ID: {} - {} {} {}-{}",
                    updatedSchedule.getId(),
                    updatedSchedule.getDayOfWeek(),
                    updatedSchedule.getRoom() != null ? updatedSchedule.getRoom().getId() : "no-room",
                    updatedSchedule.getStartTime(),
                    updatedSchedule.getEndTime());

            // Find matching original schedule by ID
            Optional<GroupSchedule> originalScheduleOpt = originalSchedules.stream()
                    .filter(s -> s.getId() != null && s.getId().equals(updatedSchedule.getId()))
                    .findFirst();

            if (originalScheduleOpt.isPresent()) {
                GroupSchedule originalSchedule = originalScheduleOpt.get();
                log.info("Found original GroupSchedule ID: {} - {} {} {}-{}",
                        originalSchedule.getId(),
                        originalSchedule.getDayOfWeek(),
                        originalSchedule.getRoom() != null ? originalSchedule.getRoom().getId() : "no-room",
                        originalSchedule.getStartTime(),
                        originalSchedule.getEndTime());

                // Check for changes
                boolean dayChanged = !originalSchedule.getDayOfWeek().equals(updatedSchedule.getDayOfWeek());
                boolean timeChanged = !originalSchedule.getStartTime().equals(updatedSchedule.getStartTime()) ||
                                    !originalSchedule.getEndTime().equals(updatedSchedule.getEndTime());
                boolean roomChanged = (originalSchedule.getRoom() == null && updatedSchedule.getRoom() != null) ||
                                    (originalSchedule.getRoom() != null && updatedSchedule.getRoom() != null &&
                                     !originalSchedule.getRoom().getId().equals(updatedSchedule.getRoom().getId()));

                log.info("GroupSchedule ID: {} changes detected - day: {}, time: {}, room: {}",
                        updatedSchedule.getId(), dayChanged, timeChanged, roomChanged);

                // Only update if there are changes
                if (dayChanged || timeChanged || roomChanged) {
                    log.info("Triggering cascade for GroupSchedule ID: {} - {} → {} | {}-{} → {}-{} | room {} → {}",
                            updatedSchedule.getId(),
                            originalSchedule.getDayOfWeek(), updatedSchedule.getDayOfWeek(),
                            originalSchedule.getStartTime(), originalSchedule.getEndTime(),
                            updatedSchedule.getStartTime(), updatedSchedule.getEndTime(),
                            originalSchedule.getRoom() != null ? originalSchedule.getRoom().getId() : "null",
                            updatedSchedule.getRoom() != null ? updatedSchedule.getRoom().getId() : "null");

                    try {
                        // Call the method from GroupScheduleService to update related schedules
                        groupScheduleService.updateRelatedSchedules(updatedSchedule, originalSchedule.getDayOfWeekEnum());
                        log.info("Cascade completed successfully for GroupSchedule ID: {}", updatedSchedule.getId());
                    } catch (Exception e) {
                        log.error("Error during cascade for GroupSchedule ID: {}", updatedSchedule.getId(), e);
                    }
                } else {
                    log.debug("No significant changes detected for GroupSchedule ID: {}, skipping cascade", updatedSchedule.getId());
                }
            } else {
                log.warn("No matching original GroupSchedule found for updated ID: {}", updatedSchedule.getId());
            }
        }
        log.info("GroupSchedule synchronization completed");
    }

    /**
     * Manually update GroupSchedules to preserve existing entity IDs for proper cascade sync
     */
    private void updateGroupSchedulesManually(Group group, Set<GroupScheduleDTO> groupScheduleDTOs) {
        log.info("Manually updating GroupSchedules for Group ID: {} with {} DTOs", 
                group.getId(), groupScheduleDTOs.size());

        // Create a map of existing GroupSchedules by ID for quick lookup
        Map<Integer, GroupSchedule> existingSchedulesById = group.getGroupSchedules().stream()
                .collect(Collectors.toMap(GroupSchedule::getId, gs -> gs));
        
        Set<GroupSchedule> updatedSchedules = new HashSet<>();
        
        for (GroupScheduleDTO dto : groupScheduleDTOs) {
            if (dto.getId() != null && existingSchedulesById.containsKey(dto.getId())) {
                // Update the existing GroupSchedule
                GroupSchedule existingSchedule = existingSchedulesById.get(dto.getId());
                log.debug("Updating existing GroupSchedule ID: {}", dto.getId());
                
                // Use mapper to update the existing entity (preserving ID)
                groupScheduleMapper.partialUpdate(dto, existingSchedule);
                updatedSchedules.add(existingSchedule);
            } else {
                // Create new GroupSchedule
                log.debug("Creating new GroupSchedule from DTO");
                GroupSchedule newSchedule = groupScheduleMapper.toEntity(dto);
                newSchedule.setGroup(group);
                updatedSchedules.add(newSchedule);
            }
        }
        
        // Replace the collection with updated schedules
        group.getGroupSchedules().clear();
        group.getGroupSchedules().addAll(updatedSchedules);
        
        log.info("Completed manual GroupSchedule update - {} schedules processed", updatedSchedules.size());
    }

    /**
     * Create a deep copy of GroupSchedule to preserve original values for comparison
     */
    private GroupSchedule createGroupScheduleCopy(GroupSchedule original) {
        GroupSchedule copy = new GroupSchedule();
        copy.setId(original.getId());
        copy.setDayOfWeek(original.getDayOfWeek());
        copy.setStartTime(original.getStartTime());
        copy.setEndTime(original.getEndTime());
        copy.setRoom(original.getRoom()); // Room reference is fine to share
        copy.setGroup(original.getGroup()); // Group reference is fine to share
        copy.setCreatedAt(original.getCreatedAt());
        copy.setUpdatedAt(original.getUpdatedAt());
        copy.setEndAt(original.getEndAt());
        return copy;
    }

    @Transactional
    @CacheEvict(value = "groups", allEntries = true)
    public void deleteGroup(Integer id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + id));
        group.softDelete();
        groupRepository.save(group);
    }

    public GroupListDTO getGroupByGroupName(String groupName) {
        String groupNamePattern = '%' + groupName + '%';
        // Use findActiveByGroupName to search for the group by name
        Group group = groupRepository.findActiveByGroupName(groupNamePattern).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group not found: " + groupName)
        );
        return groupMapper.toListDto(group);
    }
}
