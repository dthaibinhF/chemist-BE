package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.RoomDTO;
import dthaibinhf.project.chemistbe.mapper.RoomMapper;
import dthaibinhf.project.chemistbe.model.Room;
import dthaibinhf.project.chemistbe.repository.RoomRepository;
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
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomService roomService;

    private Room room;
    private RoomDTO roomDTO;

    @BeforeEach
    void setUp() {
        // Set up test data
        room = new Room();
        room.setId(1);
        room.setName("Main Hall");
        room.setLocation("Building A");
        room.setCapacity(50);

        roomDTO = new RoomDTO("Main Hall", "Building A", 50);
        // Use reflection to set the ID since RoomDTO is immutable (@Value)
        try {
            java.lang.reflect.Field idField = RoomDTO.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(roomDTO, 1);
        } catch (Exception e) {
            fail("Failed to set ID on RoomDTO: " + e.getMessage());
        }
    }

    @Test
    void testCreateRoom() {
        // Arrange
        when(roomMapper.toEntity(any(RoomDTO.class))).thenReturn(room);
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        when(roomMapper.toDto(any(Room.class))).thenReturn(roomDTO);

        // Act
        RoomDTO result = roomService.createRoom(roomDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Main Hall", result.getName());
        assertEquals("Building A", result.getLocation());
        assertEquals(50, result.getCapacity());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void testGetRoomById() {
        // Arrange
        when(roomRepository.findActiveById(1)).thenReturn(Optional.of(room));
        when(roomMapper.toDto(room)).thenReturn(roomDTO);

        // Act
        RoomDTO result = roomService.getRoomById(1);

        // Assert
        assertNotNull(result);
        assertEquals("Main Hall", result.getName());
        assertEquals("Building A", result.getLocation());
        assertEquals(50, result.getCapacity());
        verify(roomRepository, times(1)).findActiveById(1);
    }

    @Test
    void testGetRoomById_NotFound() {
        // Arrange
        when(roomRepository.findActiveById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> roomService.getRoomById(999));
        verify(roomRepository, times(1)).findActiveById(999);
    }

    @Test
    void testGetAllRooms() {
        // Arrange
        Room room2 = new Room();
        room2.setId(2);
        room2.setName("Conference Room");
        room2.setLocation("Building B");
        room2.setCapacity(30);

        RoomDTO roomDTO2 = new RoomDTO("Conference Room", "Building B", 30);
        try {
            java.lang.reflect.Field idField = RoomDTO.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(roomDTO2, 2);
        } catch (Exception e) {
            fail("Failed to set ID on RoomDTO: " + e.getMessage());
        }

        List<Room> rooms = Arrays.asList(room, room2);
        when(roomRepository.findAllActiveRooms()).thenReturn(rooms);
        when(roomMapper.toDto(room)).thenReturn(roomDTO);
        when(roomMapper.toDto(room2)).thenReturn(roomDTO2);

        // Act
        List<RoomDTO> result = roomService.getAllRooms();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Main Hall", result.get(0).getName());
        assertEquals("Conference Room", result.get(1).getName());
        verify(roomRepository, times(1)).findAllActiveRooms();
    }

    @Test
    void testDeleteRoom() {
        // Arrange
        when(roomRepository.findById(1)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        // Act
        roomService.deleteRoom(1);

        // Assert
        verify(roomRepository, times(1)).findById(1);
        verify(roomRepository, times(1)).save(room);
    }

    @Test
    void testDeleteRoom_NotFound() {
        // Arrange
        when(roomRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> roomService.deleteRoom(999));
        verify(roomRepository, times(1)).findById(999);
        verify(roomRepository, never()).save(any(Room.class));
    }
}