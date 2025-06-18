package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import dthaibinhf.project.chemistbe.model.Role;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Role}
 */
@EqualsAndHashCode(callSuper = true)
@Value
@JsonPropertyOrder({"id", "name", "create_at", "update_at", "end_at"})
public class RoleDTO extends BaseDTO implements Serializable {
    String name;
}