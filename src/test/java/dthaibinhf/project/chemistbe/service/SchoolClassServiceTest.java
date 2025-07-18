package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.SchoolClassDTO;
import dthaibinhf.project.chemistbe.mapper.SchoolClassMapper;
import dthaibinhf.project.chemistbe.model.SchoolClass;
import dthaibinhf.project.chemistbe.repository.SchoolClassRepository;
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
public class SchoolClassServiceTest {

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @Mock
    private SchoolClassMapper schoolClassMapper;

    @InjectMocks
    private SchoolClassService schoolClassService;

    private SchoolClass schoolClass;
    private SchoolClassDTO schoolClassDTO;

    @BeforeEach
    void setUp() {
        // Set up test data
        schoolClass = new SchoolClass();
        schoolClass.setId(1);
        schoolClass.setName("12A1");

        schoolClassDTO = new SchoolClassDTO("12A1");
        // Use reflection to set the ID since SchoolClassDTO is immutable (@Value)
        try {
            java.lang.reflect.Field idField = SchoolClassDTO.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(schoolClassDTO, 1);
        } catch (Exception e) {
            fail("Failed to set ID on SchoolClassDTO: " + e.getMessage());
        }
    }

    @Test
    void testCreateSchoolClass() {
        // Arrange
        when(schoolClassMapper.toEntity(any(SchoolClassDTO.class))).thenReturn(schoolClass);
        when(schoolClassRepository.save(any(SchoolClass.class))).thenReturn(schoolClass);
        when(schoolClassMapper.toDto(any(SchoolClass.class))).thenReturn(schoolClassDTO);

        // Act
        SchoolClassDTO result = schoolClassService.createSchoolClass(schoolClassDTO);

        // Assert
        assertNotNull(result);
        assertEquals("12A1", result.getName());
        verify(schoolClassRepository, times(1)).save(any(SchoolClass.class));
    }

    @Test
    void testGetSchoolClassById() {
        // Arrange
        when(schoolClassRepository.findActiveById(1)).thenReturn(Optional.of(schoolClass));
        when(schoolClassMapper.toDto(schoolClass)).thenReturn(schoolClassDTO);

        // Act
        SchoolClassDTO result = schoolClassService.getSchoolClassById(1);

        // Assert
        assertNotNull(result);
        assertEquals("12A1", result.getName());
        verify(schoolClassRepository, times(1)).findActiveById(1);
    }

    @Test
    void testGetSchoolClassById_NotFound() {
        // Arrange
        when(schoolClassRepository.findActiveById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> schoolClassService.getSchoolClassById(999));
        verify(schoolClassRepository, times(1)).findActiveById(999);
    }

    @Test
    void testGetAllSchoolClasses() {
        // Arrange
        SchoolClass schoolClass2 = new SchoolClass();
        schoolClass2.setId(2);
        schoolClass2.setName("11A2");

        SchoolClassDTO schoolClassDTO2 = new SchoolClassDTO("11A2");
        try {
            java.lang.reflect.Field idField = SchoolClassDTO.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(schoolClassDTO2, 2);
        } catch (Exception e) {
            fail("Failed to set ID on SchoolClassDTO: " + e.getMessage());
        }

        List<SchoolClass> schoolClasses = Arrays.asList(schoolClass, schoolClass2);
        when(schoolClassRepository.findAllActiveSchoolClasses()).thenReturn(schoolClasses);
        when(schoolClassMapper.toDto(schoolClass)).thenReturn(schoolClassDTO);
        when(schoolClassMapper.toDto(schoolClass2)).thenReturn(schoolClassDTO2);

        // Act
        List<SchoolClassDTO> result = schoolClassService.getAllSchoolClasses();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("12A1", result.get(0).getName());
        assertEquals("11A2", result.get(1).getName());
        verify(schoolClassRepository, times(1)).findAllActiveSchoolClasses();
    }

    @Test
    void testGetSchoolClassesByGrade() {
        // Arrange
        SchoolClass schoolClass2 = new SchoolClass();
        schoolClass2.setId(2);
        schoolClass2.setName("12A2");

        SchoolClassDTO schoolClassDTO2 = new SchoolClassDTO("12A2");
        try {
            java.lang.reflect.Field idField = SchoolClassDTO.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(schoolClassDTO2, 2);
        } catch (Exception e) {
            fail("Failed to set ID on SchoolClassDTO: " + e.getMessage());
        }

        List<SchoolClass> schoolClasses = Arrays.asList(schoolClass, schoolClass2);
        when(schoolClassRepository.findAllActiveByGrade(12)).thenReturn(schoolClasses);
        when(schoolClassMapper.toDto(schoolClass)).thenReturn(schoolClassDTO);
        when(schoolClassMapper.toDto(schoolClass2)).thenReturn(schoolClassDTO2);

        // Act
        List<SchoolClassDTO> result = schoolClassService.getSchoolClassesByGrade(12);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("12A1", result.get(0).getName());
        assertEquals("12A2", result.get(1).getName());
        verify(schoolClassRepository, times(1)).findAllActiveByGrade(12);
    }

    @Test
    void testDeleteSchoolClass() {
        // Arrange
        when(schoolClassRepository.findById(1)).thenReturn(Optional.of(schoolClass));
        when(schoolClassRepository.save(any(SchoolClass.class))).thenReturn(schoolClass);

        // Act
        schoolClassService.deleteSchoolClass(1);

        // Assert
        verify(schoolClassRepository, times(1)).findById(1);
        verify(schoolClassRepository, times(1)).save(schoolClass);
    }

    @Test
    void testDeleteSchoolClass_NotFound() {
        // Arrange
        when(schoolClassRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> schoolClassService.deleteSchoolClass(999));
        verify(schoolClassRepository, times(1)).findById(999);
        verify(schoolClassRepository, never()).save(any(SchoolClass.class));
    }
}