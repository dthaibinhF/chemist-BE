package dthaibinhf.project.chemistbe.dto;

import dthaibinhf.project.chemistbe.model.Role;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Role}
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class RoleDTO extends BaseDTO implements Serializable {
    String name;
}