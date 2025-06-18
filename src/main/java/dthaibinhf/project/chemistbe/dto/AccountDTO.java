package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Account}
 */
@EqualsAndHashCode(callSuper = true)
@Value
@JsonPropertyOrder({"id", "role_id", "name", "email", "phone", "role_name", "create_at", "update_at", "end_at"})
public class AccountDTO extends BaseDTO implements Serializable {
    String name;
    String phone;
    @Email
    String email;
    @JsonIgnore
    String password;
    @JsonProperty("role_id")
    Integer roleId;
    @JsonProperty("role_name")
    String roleName;
}