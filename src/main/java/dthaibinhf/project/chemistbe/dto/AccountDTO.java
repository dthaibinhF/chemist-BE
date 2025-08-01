package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Account}
 */
@EqualsAndHashCode(callSuper = true)
@Value
@JsonPropertyOrder({"id", "role_ids", "name", "email", "phone", "role_names", "primary_role_id", "primary_role_name", "create_at", "update_at", "end_at"})
public class AccountDTO extends BaseDTO implements Serializable {
    String name;
    String phone;
    @Email
    String email;
    @JsonIgnore
    String password;
    @JsonProperty("role_ids")
    List<Integer> roleIds;
    @JsonProperty("role_names")
    List<String> roleNames;
    @JsonProperty("primary_role_id")
    Integer primaryRoleId;
    @JsonProperty("primary_role_name")
    String primaryRoleName;
}