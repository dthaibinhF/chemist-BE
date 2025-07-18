package dthaibinhf.project.chemistbe.service;

import dthaibinhf.project.chemistbe.dto.RoleDTO;
import dthaibinhf.project.chemistbe.mapper.RoleMapper;
import dthaibinhf.project.chemistbe.model.Role;
import dthaibinhf.project.chemistbe.repository.RoleRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class RoleService {

    RoleRepository repository;
    RoleMapper mapper;

    public List<RoleDTO> getAllRoles() {
        return repository.findAllActiveRoles().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public RoleDTO getRoleById(Integer id) {
        Role role = repository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + id));
        return mapper.toDto(role);
    }

    @Transactional
    public RoleDTO updateRole(Integer id, @Valid RoleDTO roleDTO) {
        Role role = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + id));
        mapper.partialUpdate(roleDTO, role);
        return mapper.toDto(role);
    }

    @Transactional
    public RoleDTO createRole(@Valid RoleDTO roleDTO) {
        Role role = mapper.toEntity(roleDTO);
        role.setId(null);
        Role savedRole = repository.save(role);
        return mapper.toDto(savedRole);
    }

    @Transactional
    @CacheEvict(value = {"roles", "allRoles"}, key = "#id")
    public void deleteRole(Integer id) {
        Role role = repository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found: " + id));
        role.softDelete();
        repository.save(role);
    }
}