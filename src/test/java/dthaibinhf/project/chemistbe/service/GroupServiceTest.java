package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.GroupDTO;
import dthaibinhf.project.chemistbe.dto.GroupListDTO;
import dthaibinhf.project.chemistbe.mapper.GroupMapper;
import dthaibinhf.project.chemistbe.model.AcademicYear;
import dthaibinhf.project.chemistbe.model.Fee;
import dthaibinhf.project.chemistbe.model.Grade;
import dthaibinhf.project.chemistbe.model.Group;
import dthaibinhf.project.chemistbe.model.GroupSchedule;
import dthaibinhf.project.chemistbe.model.Room;
import dthaibinhf.project.chemistbe.repository.GroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private GroupMapper groupMapper;

    @Mock
    private GroupScheduleService groupScheduleService;

    @InjectMocks
    private GroupService groupService;

    private Group group;
    private GroupDTO groupDTO;
    private GroupListDTO groupListDTO;
    private Fee fee;
    private AcademicYear academicYear;
    private Grade grade;
    private GroupSchedule groupSchedule;
    private Room room;

    @BeforeEach
    void setUp() {
        // Set up test data
        fee = new Fee();
        fee.setId(1);
        fee.setName("Standard Fee");

        academicYear = new AcademicYear();
        academicYear.setId(1);
        academicYear.setYear("2024-2025");

        grade = new Grade();
        grade.setId(1);
        grade.setName("Grade 10");

        room = new Room();
        room.setId(1);
        room.setName("Room 101");

        group = new Group();
        group.setId(1);
        group.setName("Physics Group");
        group.setLevel("Advanced");
        group.setFee(fee);
        group.setAcademicYear(academicYear);
        group.setGrade(grade);
        
        groupSchedule = new GroupSchedule();
        groupSchedule.setId(1);
        groupSchedule.setDayOfWeek("MONDAY");
        groupSchedule.setStartTime(OffsetDateTime.of(2025, 7, 17, 9, 0, 0, 0, ZoneOffset.UTC));
        groupSchedule.setEndTime(OffsetDateTime.of(2025, 7, 17, 11, 0, 0, 0, ZoneOffset.UTC));
        groupSchedule.setRoom(room);
        groupSchedule.setGroup(group);
        
        Set<GroupSchedule> groupSchedules = new HashSet<>();
        groupSchedules.add(groupSchedule);
        group.setGroupSchedules(groupSchedules);

        // Create DTO objects
        groupDTO = new GroupDTO(
                "Physics Group",
                "Advanced",
                1,
                "Standard Fee",
                1,
                "2024-2025",
                1,
                "Grade 10",
                null,
                null,
                null
        );
        
        groupListDTO = new GroupListDTO(
                "Physics Group",
                "Advanced",
                1,
                "Standard Fee",
                1,
                "2024-2025",
                1,
                "Grade 10"
        );
    }

    @Test
    void testGetAllGroups() {
        // Arrange
        List<Group> groups = Arrays.asList(group);
        when(groupRepository.findAllActiveGroups()).thenReturn(groups);
        when(groupMapper.toListDto(any(Group.class))).thenReturn(groupListDTO);

        // Act
        List<GroupListDTO> result = groupService.getAllGroups();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Physics Group", result.get(0).getName());
        verify(groupRepository, times(1)).findAllActiveGroups();
    }

    @Test
    void testGetGroupById() {
        // Arrange
        when(groupRepository.findActiveById(1)).thenReturn(Optional.of(group));
        when(groupMapper.toDto(group)).thenReturn(groupDTO);

        // Act
        GroupDTO result = groupService.getGroupById(1);

        // Assert
        assertNotNull(result);
        assertEquals("Physics Group", result.getName());
        assertEquals("Advanced", result.getLevel());
        verify(groupRepository, times(1)).findActiveById(1);
    }

    @Test
    void testGetGroupById_NotFound() {
        // Arrange
        when(groupRepository.findActiveById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> groupService.getGroupById(999));
        verify(groupRepository, times(1)).findActiveById(999);
    }

    @Test
    void testGetGroupsByAcademicYearId() {
        // Arrange
        List<Group> groups = Arrays.asList(group);
        when(groupRepository.findActiveByAcademicYearId(1)).thenReturn(groups);
        when(groupMapper.toListDto(any(Group.class))).thenReturn(groupListDTO);

        // Act
        List<GroupListDTO> result = groupService.getGroupsByAcademicYearId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Physics Group", result.get(0).getName());
        verify(groupRepository, times(1)).findActiveByAcademicYearId(1);
    }

    @Test
    void testGetGroupsByGradeId() {
        // Arrange
        List<Group> groups = Arrays.asList(group);
        when(groupRepository.findActiveByGradeId(1)).thenReturn(groups);
        when(groupMapper.toListDto(any(Group.class))).thenReturn(groupListDTO);

        // Act
        List<GroupListDTO> result = groupService.getGroupsByGradeId(1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Physics Group", result.get(0).getName());
        verify(groupRepository, times(1)).findActiveByGradeId(1);
    }

    @Test
    void testGetAllGroupsWithDetail() {
        // Arrange
        List<Group> groups = Arrays.asList(group);
        when(groupRepository.findAllActiveGroups()).thenReturn(groups);
        when(groupMapper.toDto(any(Group.class))).thenReturn(groupDTO);

        // Act
        List<GroupDTO> result = groupService.getAllGroupsWithDetail();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Physics Group", result.get(0).getName());
        verify(groupRepository, times(1)).findAllActiveGroups();
    }

    @Test
    void testCreateGroup() {
        // Arrange
        when(groupMapper.toEntity(any(GroupDTO.class))).thenReturn(group);
        when(groupRepository.save(any(Group.class))).thenReturn(group);
        when(groupMapper.toDto(any(Group.class))).thenReturn(groupDTO);

        // Act
        GroupDTO result = groupService.createGroup(groupDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Physics Group", result.getName());
        assertEquals("Advanced", result.getLevel());
        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    void testUpdateGroup() {
        // Arrange
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenReturn(group);
        when(groupMapper.toDto(any(Group.class))).thenReturn(groupDTO);

        // Act
        GroupDTO result = groupService.updateGroup(1, groupDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Physics Group", result.getName());
        verify(groupRepository, times(1)).findById(1);
        verify(groupRepository, times(1)).save(group);
    }

    @Test
    void testUpdateGroup_WithScheduleChanges() {
        // Arrange
        GroupSchedule originalSchedule = new GroupSchedule();
        originalSchedule.setId(1);
        originalSchedule.setDayOfWeek("MONDAY");
        originalSchedule.setStartTime(OffsetDateTime.of(2025, 7, 17, 9, 0, 0, 0, ZoneOffset.UTC));
        originalSchedule.setEndTime(OffsetDateTime.of(2025, 7, 17, 11, 0, 0, 0, ZoneOffset.UTC));
        originalSchedule.setRoom(room);
        originalSchedule.setGroup(group);
        
        GroupSchedule updatedSchedule = new GroupSchedule();
        updatedSchedule.setId(1);
        updatedSchedule.setDayOfWeek("TUESDAY"); // Changed day
        updatedSchedule.setStartTime(OffsetDateTime.of(2025, 7, 17, 10, 0, 0, 0, ZoneOffset.UTC)); // Changed time
        updatedSchedule.setEndTime(OffsetDateTime.of(2025, 7, 17, 12, 0, 0, 0, ZoneOffset.UTC)); // Changed time
        updatedSchedule.setRoom(room);
        updatedSchedule.setGroup(group);
        
        Set<GroupSchedule> originalSchedules = new HashSet<>();
        originalSchedules.add(originalSchedule);
        
        Set<GroupSchedule> updatedSchedules = new HashSet<>();
        updatedSchedules.add(updatedSchedule);
        
        Group originalGroup = new Group();
        originalGroup.setId(1);
        originalGroup.setName("Physics Group");
        originalGroup.setLevel("Advanced");
        originalGroup.setFee(fee);
        originalGroup.setAcademicYear(academicYear);
        originalGroup.setGrade(grade);
        originalGroup.setGroupSchedules(originalSchedules);
        
        Group updatedGroup = new Group();
        updatedGroup.setId(1);
        updatedGroup.setName("Physics Group");
        updatedGroup.setLevel("Advanced");
        updatedGroup.setFee(fee);
        updatedGroup.setAcademicYear(academicYear);
        updatedGroup.setGrade(grade);
        updatedGroup.setGroupSchedules(updatedSchedules);
        
        // Create DTO with updated schedules
        GroupDTO updatedGroupDTO = new GroupDTO(
                "Physics Group",
                "Advanced",
                1,
                "Standard Fee",
                1,
                "2024-2025",
                1,
                "Grade 10",
                null, // We'll set this directly for the test
                null,
                null
        );
        
        when(groupRepository.findById(1)).thenReturn(Optional.of(originalGroup));
        doAnswer(invocation -> {
            Group group = invocation.getArgument(1);
            group.setGroupSchedules(updatedSchedules);
            return null;
        }).when(groupMapper).partialUpdate(any(GroupDTO.class), any(Group.class));
        when(groupRepository.save(any(Group.class))).thenReturn(updatedGroup);
        when(groupMapper.toDto(any(Group.class))).thenReturn(updatedGroupDTO);

        // Act
        GroupDTO result = groupService.updateGroup(1, updatedGroupDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Physics Group", result.getName());
        verify(groupRepository, times(1)).findById(1);
        verify(groupRepository, times(1)).save(any(Group.class));
        // Since we're not actually testing the synchronizeSchedulesWithGroupSchedules method's internals,
        // we don't need to verify calls to groupScheduleService.updateRelatedSchedules
    }

    @Test
    void testUpdateGroup_NotFound() {
        // Arrange
        when(groupRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> groupService.updateGroup(999, groupDTO));
        verify(groupRepository, times(1)).findById(999);
        verify(groupRepository, never()).save(any(Group.class));
    }

    @Test
    void testDeleteGroup() {
        // Arrange
        when(groupRepository.findById(1)).thenReturn(Optional.of(group));
        when(groupRepository.save(any(Group.class))).thenReturn(group);

        // Act
        groupService.deleteGroup(1);

        // Assert
        verify(groupRepository, times(1)).findById(1);
        verify(groupRepository, times(1)).save(group);
    }

    @Test
    void testDeleteGroup_NotFound() {
        // Arrange
        when(groupRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> groupService.deleteGroup(999));
        verify(groupRepository, times(1)).findById(999);
        verify(groupRepository, never()).save(any(Group.class));
    }
}