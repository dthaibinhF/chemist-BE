package dthaibinhf.project.chemistbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dthaibinhf.project.chemistbe.dto.RoomDTO;
import dthaibinhf.project.chemistbe.dto.ScheduleDTO;
import dthaibinhf.project.chemistbe.model.*;
import dthaibinhf.project.chemistbe.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private GroupScheduleRepository groupScheduleRepository;

    @Autowired
    private FeeRepository feeRepository;

    // Test data
    private Group testGroup;
    private Room testRoom;
    private Schedule testSchedule;
    private LocalDate today;
    private LocalDate tomorrow;
    private OffsetDateTime baseStartTime;
    private OffsetDateTime baseEndTime;

    @BeforeEach
    void setUp() {
        // Clean up repositories
        scheduleRepository.deleteAll();
        groupScheduleRepository.deleteAll();
        groupRepository.deleteAll();
        feeRepository.deleteAll();
        roomRepository.deleteAll();
        academicYearRepository.deleteAll();
        gradeRepository.deleteAll();

        // Setup test dates and times
        today = LocalDate.now();
        tomorrow = today.plusDays(1);
        baseStartTime = tomorrow.atTime(9, 0).atOffset(ZoneOffset.ofHours(7));
        baseEndTime = tomorrow.atTime(10, 0).atOffset(ZoneOffset.ofHours(7));

        // Create base entities
        setupBaseEntities();
        setupTestScheduleData();
    }

    private void setupBaseEntities() {
        // Create AcademicYear
        AcademicYear academicYear = new AcademicYear();
        academicYear.setYear("2025");
        academicYear = academicYearRepository.save(academicYear);

        // Create Grade
        Grade grade = new Grade();
        grade.setName("Grade 10");
        grade = gradeRepository.save(grade);

        // Create Fee
        Fee fee = new Fee();
        fee.setName("Test Fee");
        fee.setDescription("Test Fee Description");
        fee.setAmount(java.math.BigDecimal.valueOf(100.00));
        fee.setStartTime(OffsetDateTime.now());
        fee.setEndTime(OffsetDateTime.now().plusMonths(1));
        fee = feeRepository.save(fee);

        // Create Group
        testGroup = new Group();
        testGroup.setName("Test Group");
        testGroup.setLevel("Beginner");
        testGroup.setAcademicYear(academicYear);
        testGroup.setGrade(grade);
        testGroup.setFee(fee);
        testGroup = groupRepository.save(testGroup);

        // Create Room
        testRoom = new Room();
        testRoom.setName("Room A");
        testRoom.setLocation("Building 1");
        testRoom.setCapacity(30);
        testRoom = roomRepository.save(testRoom);
    }

    private void setupTestScheduleData() {
        // Create test schedule entity
        testSchedule = Schedule.builder()
                .group(testGroup)
                .startTime(baseStartTime)
                .endTime(baseEndTime)
                .deliveryMode("OFFLINE")
                .teacher(null)
                .room(testRoom)
                .meetingLink(null)
                .build();
    }

    private ScheduleDTO createValidScheduleDTO() {
        RoomDTO roomDTO = new RoomDTO(testRoom.getName(), testRoom.getLocation(), testRoom.getCapacity());
        roomDTO.setId(testRoom.getId()); // Set the room ID which is required by service validation
        
        return new ScheduleDTO(
                testGroup.getId(),
                testGroup.getName(),
                baseStartTime,
                baseEndTime,
                "OFFLINE",
                null,
                null,
                null,
                roomDTO
        );
    }

    // Basic CRUD Tests

    @Test
    @WithMockUser
    void getAllSchedules_ShouldReturnActiveSchedulesOnly() throws Exception {
        // Create active schedule
        Schedule activeSchedule = scheduleRepository.save(testSchedule);

        // Create soft-deleted schedule
        Schedule deletedSchedule = Schedule.builder()
                .group(testGroup)
                .startTime(baseStartTime.plusHours(2))
                .endTime(baseEndTime.plusHours(2))
                .deliveryMode("ONLINE")
                .teacher(null)
                .room(testRoom)
                .meetingLink("https://meet.example.com")
                .build();
        deletedSchedule.softDelete();
        scheduleRepository.save(deletedSchedule);

        mockMvc.perform(get("/api/v1/schedule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(activeSchedule.getId())))
                .andExpect(jsonPath("$[0].group_id", is(testGroup.getId())))
                .andExpect(jsonPath("$[0].delivery_mode", is("OFFLINE")));
    }

    @Test
    @WithMockUser
    void getAllSchedules_EmptyDatabase_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/schedule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void getScheduleById_ExistingSchedule_ShouldReturnSchedule() throws Exception {
        Schedule savedSchedule = scheduleRepository.save(testSchedule);

        mockMvc.perform(get("/api/v1/schedule/{id}", savedSchedule.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedSchedule.getId())))
                .andExpect(jsonPath("$.group_id", is(testGroup.getId())))
                .andExpect(jsonPath("$.group_name", is(testGroup.getName())))
                .andExpect(jsonPath("$.delivery_mode", is("OFFLINE")))
                .andExpect(jsonPath("$.room.name", is(testRoom.getName())));
    }

    @Test
    @WithMockUser
    void getScheduleById_NonExistingSchedule_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/schedule/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getScheduleById_SoftDeletedSchedule_ShouldReturnNotFound() throws Exception {
        testSchedule.softDelete();
        Schedule savedSchedule = scheduleRepository.save(testSchedule);

        mockMvc.perform(get("/api/v1/schedule/{id}", savedSchedule.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createSchedule_ValidRequest_ShouldReturnCreatedSchedule() throws Exception {
        ScheduleDTO validDTO = createValidScheduleDTO();

        mockMvc.perform(post("/api/v1/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.group_id", is(testGroup.getId())))
                .andExpect(jsonPath("$.delivery_mode", is("OFFLINE")))
                .andExpect(jsonPath("$.room.name", is(testRoom.getName())))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @WithMockUser
    void createSchedule_MissingGroupId_ShouldReturnBadRequest() throws Exception {
        RoomDTO roomDTO = new RoomDTO(testRoom.getName(), testRoom.getLocation(), testRoom.getCapacity());
        roomDTO.setId(testRoom.getId());
        
        ScheduleDTO invalidDTO = new ScheduleDTO(
                null, // missing groupId
                null,
                baseStartTime,
                baseEndTime,
                "OFFLINE",
                null,
                null,
                null,
                roomDTO
        );

        mockMvc.perform(post("/api/v1/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createSchedule_InvalidDeliveryMode_ShouldReturnBadRequest() throws Exception {
        RoomDTO roomDTO = new RoomDTO(testRoom.getName(), testRoom.getLocation(), testRoom.getCapacity());
        roomDTO.setId(testRoom.getId());
        
        ScheduleDTO invalidDTO = new ScheduleDTO(
                testGroup.getId(),
                testGroup.getName(),
                baseStartTime,
                baseEndTime,
                "INVALID_MODE",
                null,
                null,
                null,
                roomDTO
        );

        mockMvc.perform(post("/api/v1/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createSchedule_StartTimeAfterEndTime_ShouldReturnBadRequest() throws Exception {
        RoomDTO roomDTO = new RoomDTO(testRoom.getName(), testRoom.getLocation(), testRoom.getCapacity());
        roomDTO.setId(testRoom.getId());
        
        ScheduleDTO invalidDTO = new ScheduleDTO(
                testGroup.getId(),
                testGroup.getName(),
                baseEndTime, // start time after end time
                baseStartTime, // end time before start time
                "OFFLINE",
                null,
                null,
                null,
                roomDTO
        );

        mockMvc.perform(post("/api/v1/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateSchedule_ValidRequest_ShouldReturnUpdatedSchedule() throws Exception {
        Schedule savedSchedule = scheduleRepository.save(testSchedule);
        
        RoomDTO roomDTO = new RoomDTO(testRoom.getName(), testRoom.getLocation(), testRoom.getCapacity());
        roomDTO.setId(testRoom.getId()); // Set the room ID which is required by service validation
        
        ScheduleDTO updateDTO = new ScheduleDTO(
                testGroup.getId(),
                testGroup.getName(),
                baseStartTime.plusHours(1),
                baseEndTime.plusHours(1),
                "ONLINE",
                "https://meet.updated.com",
                null,
                null,
                roomDTO
        );

        mockMvc.perform(put("/api/v1/schedule/{id}", savedSchedule.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedSchedule.getId())))
                .andExpect(jsonPath("$.delivery_mode", is("ONLINE")))
                .andExpect(jsonPath("$.meeting_link", is("https://meet.updated.com")));
    }

    @Test
    @WithMockUser
    void updateSchedule_NonExistingSchedule_ShouldReturnNotFound() throws Exception {
        ScheduleDTO updateDTO = createValidScheduleDTO();

        mockMvc.perform(put("/api/v1/schedule/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteSchedule_ExistingSchedule_ShouldReturnNoContent() throws Exception {
        Schedule savedSchedule = scheduleRepository.save(testSchedule);

        mockMvc.perform(delete("/api/v1/schedule/{id}", savedSchedule.getId()))
                .andExpect(status().isNoContent());

        // Verify schedule is soft deleted
        mockMvc.perform(get("/api/v1/schedule/{id}", savedSchedule.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void deleteSchedule_NonExistingSchedule_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/schedule/{id}", 999))
                .andExpect(status().isNotFound());
    }

    // Search Tests with LocalDate Parameters

    @Test
    @WithMockUser
    void searchSchedules_NoFilters_ShouldReturnAllSchedules() throws Exception {
        scheduleRepository.save(testSchedule);
        
        Schedule schedule2 = Schedule.builder()
                .group(testGroup)
                .startTime(baseStartTime.plusDays(1))
                .endTime(baseEndTime.plusDays(1))
                .deliveryMode("ONLINE")
                .teacher(null)
                .room(testRoom)
                .meetingLink("https://meet.example.com")
                .build();
        scheduleRepository.save(schedule2);

        mockMvc.perform(get("/api/v1/schedule/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    void searchSchedules_FilterByGroupId_ShouldReturnFilteredSchedules() throws Exception {
        // Create another group
        Group anotherGroup = new Group();
        anotherGroup.setName("Another Group");
        anotherGroup.setLevel("Advanced");
        anotherGroup.setAcademicYear(testGroup.getAcademicYear());
        anotherGroup.setGrade(testGroup.getGrade());
        anotherGroup.setFee(testGroup.getFee()); // Set the required Fee
        anotherGroup = groupRepository.save(anotherGroup);

        // Create schedules for both groups
        scheduleRepository.save(testSchedule);

        Schedule anotherSchedule = Schedule.builder()
                .group(anotherGroup)
                .startTime(baseStartTime.plusHours(2))
                .endTime(baseEndTime.plusHours(2))
                .deliveryMode("OFFLINE")
                .teacher(null)
                .room(testRoom)
                .build();
        scheduleRepository.save(anotherSchedule);

        mockMvc.perform(get("/api/v1/schedule/search")
                .param("groupId", testGroup.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].group_id", is(testGroup.getId())));
    }

    @Test
    @WithMockUser
    void searchSchedules_FilterByDateRange_ShouldReturnSchedulesInRange() throws Exception {
        // Create schedules on different dates
        Schedule todaySchedule = Schedule.builder()
                .group(testGroup)
                .startTime(today.atTime(9, 0).atOffset(ZoneOffset.ofHours(7)))
                .endTime(today.atTime(10, 0).atOffset(ZoneOffset.ofHours(7)))
                .deliveryMode("OFFLINE")
                .teacher(null)
                .room(testRoom)
                .build();
        scheduleRepository.save(todaySchedule);

        scheduleRepository.save(testSchedule); // tomorrow

        Schedule futureSchedule = Schedule.builder()
                .group(testGroup)
                .startTime(today.plusDays(7).atTime(9, 0).atOffset(ZoneOffset.ofHours(7)))
                .endTime(today.plusDays(7).atTime(10, 0).atOffset(ZoneOffset.ofHours(7)))
                .deliveryMode("OFFLINE")
                .teacher(null)
                .room(testRoom)
                .build();
        scheduleRepository.save(futureSchedule);

        // Search for schedules from today to tomorrow
        mockMvc.perform(get("/api/v1/schedule/search")
                .param("startDate", today.toString())
                .param("endDate", tomorrow.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser
    void searchSchedules_InvalidDateRange_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/schedule/search")
                .param("startDate", tomorrow.toString())
                .param("endDate", today.toString())) // end before start
                .andExpect(status().isBadRequest());
    }

    // Weekly Generation Tests

    @Test
    @WithMockUser
    void generateWeeklySchedule_NonExistingGroup_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(post("/api/v1/schedule/weekly")
                .param("groupId", "999")
                .param("startDate", today.toString())
                .param("endDate", tomorrow.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void generateWeeklySchedule_InvalidDateRange_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/schedule/weekly")
                .param("groupId", testGroup.getId().toString())
                .param("startDate", tomorrow.toString())
                .param("endDate", today.toString())) // end before start
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void generateWeeklySchedule_GroupWithoutTemplates_ShouldReturnEmptySet() throws Exception {
        mockMvc.perform(post("/api/v1/schedule/weekly")
                .param("groupId", testGroup.getId().toString())
                .param("startDate", today.toString())
                .param("endDate", tomorrow.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}