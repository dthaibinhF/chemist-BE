package dthaibinhf.project.chemistbe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    String name;
    String phone;
    String email;
    String password;
    @JsonProperty("role_ids")
    List<Integer> roleIds;
}
