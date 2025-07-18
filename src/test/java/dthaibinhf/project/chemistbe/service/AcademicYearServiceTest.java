package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.AcademicYearDTO;
import dthaibinhf.project.chemistbe.mapper.AcademicYearMapper;
import dthaibinhf.project.chemistbe.model.AcademicYear;
import dthaibinhf.project.chemistbe.repository.AcademicYearRepository;
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
public class AcademicYearServiceTest {

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Mock
    private AcademicYearMapper academicYearMapper;

    @InjectMocks
    private AcademicYearService academicYearService;

    private AcademicYear academicYear;
    private AcademicYearDTO academicYearDTO;

    @BeforeEach
    void setUp() {
        // Set up test data
        academicYear = new AcademicYear();
        academicYear.setId(1);
        academicYear.setYear("2023-2024");

        academicYearDTO = new AcademicYearDTO("2023-2024");
        // Use reflection to set the ID since AcademicYearDTO is immutable (@Value)
        try {
            java.lang.reflect.Field idField = AcademicYearDTO.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(academicYearDTO, 1);
        } catch (Exception e) {
            fail("Failed to set ID on AcademicYearDTO: " + e.getMessage());
        }
    }

    @Test
    void testCreateAcademicYear() {
        // Arrange
        when(academicYearMapper.toEntity(any(AcademicYearDTO.class))).thenReturn(academicYear);
        when(academicYearRepository.save(any(AcademicYear.class))).thenReturn(academicYear);
        when(academicYearMapper.toDto(any(AcademicYear.class))).thenReturn(academicYearDTO);

        // Act
        AcademicYearDTO result = academicYearService.createAcademicYear(academicYearDTO);

        // Assert
        assertNotNull(result);
        assertEquals("2023-2024", result.getYear());
        verify(academicYearRepository, times(1)).save(any(AcademicYear.class));
    }

    @Test
    void testGetAcademicYearById() {
        // Arrange
        when(academicYearRepository.findActiveById(1)).thenReturn(Optional.of(academicYear));
        when(academicYearMapper.toDto(academicYear)).thenReturn(academicYearDTO);

        // Act
        AcademicYearDTO result = academicYearService.getAcademicYearById(1);

        // Assert
        assertNotNull(result);
        assertEquals("2023-2024", result.getYear());
        verify(academicYearRepository, times(1)).findActiveById(1);
    }

    @Test
    void testGetAcademicYearById_NotFound() {
        // Arrange
        when(academicYearRepository.findActiveById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> academicYearService.getAcademicYearById(999));
        verify(academicYearRepository, times(1)).findActiveById(999);
    }

    @Test
    void testGetAllAcademicYears() {
        // Arrange
        AcademicYear academicYear2 = new AcademicYear();
        academicYear2.setId(2);
        academicYear2.setYear("2024-2025");

        AcademicYearDTO academicYearDTO2 = new AcademicYearDTO("2024-2025");
        try {
            java.lang.reflect.Field idField = AcademicYearDTO.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(academicYearDTO2, 2);
        } catch (Exception e) {
            fail("Failed to set ID on AcademicYearDTO: " + e.getMessage());
        }

        List<AcademicYear> academicYears = Arrays.asList(academicYear, academicYear2);
        when(academicYearRepository.findAllActiveAcademicYears()).thenReturn(academicYears);
        when(academicYearMapper.toDto(academicYear)).thenReturn(academicYearDTO);
        when(academicYearMapper.toDto(academicYear2)).thenReturn(academicYearDTO2);

        // Act
        List<AcademicYearDTO> result = academicYearService.getAllAcademicYears();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("2023-2024", result.get(0).getYear());
        assertEquals("2024-2025", result.get(1).getYear());
        verify(academicYearRepository, times(1)).findAllActiveAcademicYears();
    }

    @Test
    void testDeleteAcademicYear() {
        // Arrange
        when(academicYearRepository.findById(1)).thenReturn(Optional.of(academicYear));
        when(academicYearRepository.save(any(AcademicYear.class))).thenReturn(academicYear);

        // Act
        academicYearService.deleteAcademicYear(1);

        // Assert
        verify(academicYearRepository, times(1)).findById(1);
        verify(academicYearRepository, times(1)).save(academicYear);
    }

    @Test
    void testDeleteAcademicYear_NotFound() {
        // Arrange
        when(academicYearRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> academicYearService.deleteAcademicYear(999));
        verify(academicYearRepository, times(1)).findById(999);
        verify(academicYearRepository, never()).save(any(AcademicYear.class));
    }
}