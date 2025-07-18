package dthaibinhf.project.chemistbe.controller;

import dthaibinhf.project.chemistbe.dto.RoleDTO;
import dthaibinhf.project.chemistbe.service.RoleService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class RoleController {

    RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(roleService.createRole(roleDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRole(@PathVariable Integer id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Integer id, @Valid @RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(roleService.updateRole(id, roleDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
