package dthaibinhf.project.chemistbe.dto.response;

import dthaibinhf.project.chemistbe.dto.RoleDTO;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterResponse {
    Integer id;
    String email;
    String name;
    String phone;
    RoleDTO roles;
    LocalDate createAt;
    LocalDate updateAt;
}
