package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.RoomDTO;
import dthaibinhf.project.chemistbe.mapper.RoomMapper;
import dthaibinhf.project.chemistbe.model.Room;
import dthaibinhf.project.chemistbe.repository.RoomRepository;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class RoomService {

    RoomRepository roomRepository;
    RoomMapper roomMapper;

    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAllActiveRooms().stream()
                .map(roomMapper::toDto)
                .collect(Collectors.toList());
    }

    public RoomDTO getRoomById(Integer id) {
        Room room = roomRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found: " + id));
        return roomMapper.toDto(room);
    }

    @Transactional
    public RoomDTO createRoom(@Valid RoomDTO roomDTO) {
        Room room = roomMapper.toEntity(roomDTO);
        room.setId(null);
        Room savedRoom = roomRepository.save(room);
        return roomMapper.toDto(savedRoom);
    }

    @Transactional
    public RoomDTO updateRoom(Integer id, @Valid RoomDTO roomDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found: " + id));
        roomMapper.partialUpdate(roomDTO, room);
        Room updatedRoom = roomRepository.save(room);
        return roomMapper.toDto(updatedRoom);
    }

    @Transactional
    public void deleteRoom(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found: " + id));
        room.softDelete();
        roomRepository.save(room);
    }
}