package dthaibinhf.project.chemistbe.dto.response;

import dthaibinhf.project.chemistbe.dto.RoleDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterResponse {
    Integer accountId;
    String email;
    String fullName;
    String phone;
    Set<RoleDTO> roles;
    LocalDate createAt;
    LocalDate updateAt;
}
