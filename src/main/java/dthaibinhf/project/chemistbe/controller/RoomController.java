package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.RoomDTO;
import dthaibinhf.project.chemistbe.service.RoomService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/room")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class RoomController {

    RoomService roomService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody RoomDTO roomDTO) {
        return ResponseEntity.ok(roomService.createRoom(roomDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable Integer id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable Integer id, @Valid @RequestBody RoomDTO roomDTO) {
        return ResponseEntity.ok(roomService.updateRoom(id, roomDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}