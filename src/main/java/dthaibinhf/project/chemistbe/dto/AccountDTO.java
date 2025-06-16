package dthaibinhf.project.chemistbe.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Account}
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class AccountDTO extends BaseDTO implements Serializable {
    RoleDTO role;
    String name;
    String phone;
    String email;
    String password;
}