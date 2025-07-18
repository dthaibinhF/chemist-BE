package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.SchoolDTO;
import dthaibinhf.project.chemistbe.mapper.SchoolMapper;
import dthaibinhf.project.chemistbe.model.School;
import dthaibinhf.project.chemistbe.repository.SchoolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SchoolServiceTest {

    @Mock
    private SchoolRepository schoolRepository;

    @Mock
    private SchoolMapper schoolMapper;

    @InjectMocks
    private SchoolService schoolService;

    private School school;
    private SchoolDTO schoolDTO;

    @BeforeEach
    void setUp() {
        // Set up test data
        school = new School();
        school.setId(1);
        school.setName("Test School");

        schoolDTO = new SchoolDTO("Test School");
        // Use reflection to set the ID since SchoolDTO is immutable (@Value)
        try {
            java.lang.reflect.Field idField = SchoolDTO.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(schoolDTO, 1);
        } catch (Exception e) {
            fail("Failed to set ID on SchoolDTO: " + e.getMessage());
        }
    }

    @Test
    void testCreateSchool() {
        // Arrange
        when(schoolRepository.findSchoolByName("Test School")).thenReturn(Optional.empty());
        when(schoolMapper.toEntity(any(SchoolDTO.class))).thenReturn(school);
        when(schoolRepository.save(any(School.class))).thenReturn(school);
        when(schoolMapper.toDto(any(School.class))).thenReturn(schoolDTO);

        // Act
        SchoolDTO result = schoolService.createSchool(schoolDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test School", result.getName());
        verify(schoolRepository, times(1)).findSchoolByName("Test School");
        verify(schoolRepository, times(1)).save(any(School.class));
    }

    @Test
    void testCreateSchool_NameAlreadyExists() {
        // Arrange
        when(schoolRepository.findSchoolByName("Test School")).thenReturn(Optional.of(school));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> schoolService.createSchool(schoolDTO));
        verify(schoolRepository, times(1)).findSchoolByName("Test School");
        verify(schoolRepository, never()).save(any(School.class));
    }

    @Test
    void testGetSchoolById() {
        // Arrange
        when(schoolRepository.findActiveById(1)).thenReturn(Optional.of(school));
        when(schoolMapper.toDto(school)).thenReturn(schoolDTO);

        // Act
        SchoolDTO result = schoolService.getSchoolById(1);

        // Assert
        assertNotNull(result);
        assertEquals("Test School", result.getName());
        verify(schoolRepository, times(1)).findActiveById(1);
    }

    @Test
    void testGetSchoolById_NotFound() {
        // Arrange
        when(schoolRepository.findActiveById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> schoolService.getSchoolById(999));
        verify(schoolRepository, times(1)).findActiveById(999);
    }

    @Test
    void testGetAllSchools() {
        // Arrange
        School school2 = new School();
        school2.setId(2);
        school2.setName("Another School");

        SchoolDTO schoolDTO2 = new SchoolDTO("Another School");
        try {
            java.lang.reflect.Field idField = SchoolDTO.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(schoolDTO2, 2);
        } catch (Exception e) {
            fail("Failed to set ID on SchoolDTO: " + e.getMessage());
        }

        List<School> schools = Arrays.asList(school, school2);
        when(schoolRepository.findAllActiveSchools()).thenReturn(schools);
        when(schoolMapper.toDto(school)).thenReturn(schoolDTO);
        when(schoolMapper.toDto(school2)).thenReturn(schoolDTO2);

        // Act
        List<SchoolDTO> result = schoolService.getAllSchools();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test School", result.get(0).getName());
        assertEquals("Another School", result.get(1).getName());
        verify(schoolRepository, times(1)).findAllActiveSchools();
    }

    @Test
    void testDeleteSchool() {
        // Arrange
        when(schoolRepository.findById(1)).thenReturn(Optional.of(school));
        when(schoolRepository.save(any(School.class))).thenReturn(school);

        // Act
        schoolService.deleteSchool(1);

        // Assert
        verify(schoolRepository, times(1)).findById(1);
        verify(schoolRepository, times(1)).save(school);
    }

    @Test
    void testDeleteSchool_NotFound() {
        // Arrange
        when(schoolRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> schoolService.deleteSchool(999));
        verify(schoolRepository, times(1)).findById(999);
        verify(schoolRepository, never()).save(any(School.class));
    }
}