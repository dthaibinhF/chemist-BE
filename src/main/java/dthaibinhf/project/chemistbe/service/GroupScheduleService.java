package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.GroupScheduleDTO;
import dthaibinhf.project.chemistbe.mapper.GroupScheduleMapper;
import dthaibinhf.project.chemistbe.model.GroupSchedule;
import dthaibinhf.project.chemistbe.repository.GroupScheduleRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GroupScheduleService {

    GroupScheduleRepository groupScheduleRepository;
    GroupScheduleMapper groupScheduleMapper;


    public List<GroupScheduleDTO> getAllGroupSchedules() {
        List<GroupSchedule> groupSchedules = groupScheduleRepository.findAllActiveGroupSchedule();
        return groupSchedules.stream().map(groupScheduleMapper::toDto).collect(Collectors.toList());
    }

    public GroupScheduleDTO getGroupScheduleById(Integer id) {
        GroupSchedule groupSchedule = groupScheduleRepository.findActiveById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group schedule not found with id: " + id)
        );
        return groupScheduleMapper.toDto(groupSchedule);
    }

    public List<GroupScheduleDTO> getGroupScheduleByGroupId(Integer groupId) {
        return groupScheduleRepository.findAllActiveByGroupId(groupId).stream().map(groupScheduleMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public GroupScheduleDTO createGroupSchedule(@Valid GroupScheduleDTO groupScheduleDTO) {
        GroupSchedule groupSchedule = groupScheduleMapper.toEntity(groupScheduleDTO);
        groupSchedule.setId(null);
        groupScheduleRepository.save(groupSchedule);
        return groupScheduleMapper.toDto(groupSchedule);
    }

    @Transactional
    public GroupScheduleDTO updateGroupSchedule(Integer id, @Valid GroupScheduleDTO groupScheduleDTO) {
        GroupSchedule groupSchedule = groupScheduleRepository.findActiveById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group schedule not found with id: " + id)
        );
        groupScheduleMapper.partialUpdate(groupScheduleDTO, groupSchedule);
        return groupScheduleMapper.toDto(groupSchedule);
    }

    @Transactional
    public void deleteGroupSchedule(Integer id) {
        GroupSchedule groupSchedule = groupScheduleRepository.findActiveById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Group schedule not found with id: " + id)
        );
        groupSchedule.softDelete();
        groupScheduleRepository.save(groupSchedule);
    }

}
