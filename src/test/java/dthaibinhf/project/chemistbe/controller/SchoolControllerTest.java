package dthaibinhf.project.chemistbe.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dthaibinhf.project.chemistbe.dto.SchoolDTO;
import dthaibinhf.project.chemistbe.model.School;
import dthaibinhf.project.chemistbe.repository.SchoolRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
class SchoolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SchoolRepository schoolRepository;

    private SchoolDTO testSchoolDTO;
    private School testSchool;

    @BeforeEach
    void setUp() {
        schoolRepository.deleteAll();
        
        testSchoolDTO = new SchoolDTO("Test School");
        
        testSchool = new School();
        testSchool.setName("Existing School");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSchool_ValidRequest_ShouldReturnCreatedSchool() throws Exception {
        mockMvc.perform(post("/api/v1/school")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSchoolDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test School")))
                .andExpect(jsonPath("$.id", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void createSchool_WithManagerRole_ShouldReturnCreatedSchool() throws Exception {
        mockMvc.perform(post("/api/v1/school")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSchoolDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test School")));
    }

    @Test
    @WithMockUser(roles = "TESTER")
    void createSchool_WithTesterRole_ShouldReturnCreatedSchool() throws Exception {
        mockMvc.perform(post("/api/v1/school")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSchoolDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test School")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createSchool_WithInsufficientRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/school")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSchoolDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createSchool_DuplicateName_ShouldReturnConflict() throws Exception {
        schoolRepository.save(testSchool);

        SchoolDTO duplicateSchoolDTO = new SchoolDTO("Existing School");

        mockMvc.perform(post("/api/v1/school")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateSchoolDTO)))
                .andExpect(status().isConflict());
    }


    @Test
    @WithMockUser
    void getSchoolById_ExistingSchool_ShouldReturnSchool() throws Exception {
        School savedSchool = schoolRepository.save(testSchool);

        mockMvc.perform(get("/api/v1/school/{id}", savedSchool.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedSchool.getId())))
                .andExpect(jsonPath("$.name", is("Existing School")));
    }

    @Test
    @WithMockUser
    void getSchoolById_NonExistingSchool_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/school/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getSchoolById_SoftDeletedSchool_ShouldReturnNotFound() throws Exception {
        testSchool.softDelete();
        School savedSchool = schoolRepository.save(testSchool);

        mockMvc.perform(get("/api/v1/school/{id}", savedSchool.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getAllSchools_ShouldReturnActiveSchoolsOnly() throws Exception {
        School activeSchool1 = new School();
        activeSchool1.setName("Active School 1");
        
        School activeSchool2 = new School();
        activeSchool2.setName("Active School 2");
        
        School deletedSchool = new School();
        deletedSchool.setName("Deleted School");
        deletedSchool.softDelete();

        schoolRepository.save(activeSchool1);
        schoolRepository.save(activeSchool2);
        schoolRepository.save(deletedSchool);

        mockMvc.perform(get("/api/v1/school"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Active School 1", "Active School 2")));
    }

    @Test
    @WithMockUser
    void getAllSchools_EmptyDatabase_ShouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/api/v1/school"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void updateSchool_ValidRequest_ShouldReturnUpdatedSchool() throws Exception {
        School savedSchool = schoolRepository.save(testSchool);
        SchoolDTO updateDTO = new SchoolDTO("Updated School Name");

        mockMvc.perform(put("/api/v1/school/{id}", savedSchool.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedSchool.getId())))
                .andExpect(jsonPath("$.name", is("Updated School Name")));
    }

    @Test
    @WithMockUser
    void updateSchool_NonExistingSchool_ShouldReturnNotFound() throws Exception {
        SchoolDTO updateDTO = new SchoolDTO("Updated School Name");

        mockMvc.perform(put("/api/v1/school/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSchool_ExistingSchool_ShouldReturnNoContent() throws Exception {
        School savedSchool = schoolRepository.save(testSchool);

        mockMvc.perform(delete("/api/v1/school/{id}", savedSchool.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/school/{id}", savedSchool.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "TESTER")
    void deleteSchool_WithTesterRole_ShouldReturnNoContent() throws Exception {
        School savedSchool = schoolRepository.save(testSchool);

        mockMvc.perform(delete("/api/v1/school/{id}", savedSchool.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void deleteSchool_WithInsufficientRole_ShouldReturnForbidden() throws Exception {
        School savedSchool = schoolRepository.save(testSchool);

        mockMvc.perform(delete("/api/v1/school/{id}", savedSchool.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteSchool_NonExistingSchool_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/school/{id}", 999))
                .andExpect(status().isNotFound());
    }
}