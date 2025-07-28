package dthaibinhf.project.chemistbe.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgetPasswordRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @JsonProperty("email")
    String email;
}