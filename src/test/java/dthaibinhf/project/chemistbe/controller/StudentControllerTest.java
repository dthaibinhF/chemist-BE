package dthaibinhf.project.chemistbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dthaibinhf.project.chemistbe.dto.StudentDTO;
import dthaibinhf.project.chemistbe.model.StudentDetailDTO;
import dthaibinhf.project.chemistbe.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class StudentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StudentService studentService;

    @InjectMocks
    private StudentController studentController;

    private ObjectMapper objectMapper;

    private StudentDTO studentDTO;
    private List<StudentDTO> studentDTOList;
    private List<StudentDetailDTO> studentDetailDTOList;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(studentController).build();

        // Initialize ObjectMapper
        objectMapper = new ObjectMapper();

        // Create a sample StudentDTO for testing
        studentDTO = new StudentDTO(
                "Test Student",
                "1234567890",
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>()
        );

        // Create a list of StudentDTOs for testing
        studentDTOList = Arrays.asList(
                studentDTO,
                new StudentDTO(
                        "Another Student",
                        "0987654321",
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>()
                )
        );

        // Create a list of StudentDetailDTOs for testing
        studentDetailDTOList = Collections.emptyList(); // Simplified for now
    }

    @Test
    void getAllStudents_ShouldReturnListOfStudents() throws Exception {
        when(studentService.getAllStudents()).thenReturn(studentDTOList);

        mockMvc.perform(get("/api/v1/student"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Student")))
                .andExpect(jsonPath("$[1].name", is("Another Student")));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void getStudentById_ShouldReturnStudent() throws Exception {
        when(studentService.getStudentById(1)).thenReturn(studentDTO);

        mockMvc.perform(get("/api/v1/student/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Student")))
                .andExpect(jsonPath("$.parent_phone", is("1234567890")));

        verify(studentService, times(1)).getStudentById(1);
    }

    @Test
    void createStudent_ShouldReturnCreatedStudent() throws Exception {
        when(studentService.createStudent(any(StudentDTO.class))).thenReturn(studentDTO);

        mockMvc.perform(post("/api/v1/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Student")))
                .andExpect(jsonPath("$.parent_phone", is("1234567890")));

        verify(studentService, times(1)).createStudent(any(StudentDTO.class));
    }

    @Test
    void createMultipleStudent_ShouldReturnCreatedStudents() throws Exception {
        when(studentService.createMultipleStudent(anyList())).thenReturn(studentDTOList);

        mockMvc.perform(post("/api/v1/student/multiple")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDTOList)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Student")))
                .andExpect(jsonPath("$[1].name", is("Another Student")));

        verify(studentService, times(1)).createMultipleStudent(anyList());
    }

    @Test
    void updateStudent_ShouldReturnUpdatedStudent() throws Exception {
        when(studentService.updateStudent(eq(1), any(StudentDTO.class))).thenReturn(studentDTO);

        mockMvc.perform(put("/api/v1/student/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Test Student")))
                .andExpect(jsonPath("$.parent_phone", is("1234567890")));

        verify(studentService, times(1)).updateStudent(eq(1), any(StudentDTO.class));
    }

    @Test
    void deleteStudent_ShouldReturnNoContent() throws Exception {
        doNothing().when(studentService).deleteStudent(1);

        mockMvc.perform(delete("/api/v1/student/1"))
                .andExpect(status().isNoContent());

        verify(studentService, times(1)).deleteStudent(1);
    }

    @Test
    void getStudentsByGroupId_ShouldReturnListOfStudents() throws Exception {
        when(studentService.getStudentsByGroupId(1)).thenReturn(studentDTOList);

        mockMvc.perform(get("/api/v1/student/by-group/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Test Student")))
                .andExpect(jsonPath("$[1].name", is("Another Student")));

        verify(studentService, times(1)).getStudentsByGroupId(1);
    }

    @Test
    void getStudentDetailHistory_ShouldReturnListOfStudentDetails() throws Exception {
        when(studentService.getStudentDetailHistory(1)).thenReturn(studentDetailDTOList);

        mockMvc.perform(get("/api/v1/student/1/detail-history"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0))); // Empty list for now

        verify(studentService, times(1)).getStudentDetailHistory(1);
    }
}