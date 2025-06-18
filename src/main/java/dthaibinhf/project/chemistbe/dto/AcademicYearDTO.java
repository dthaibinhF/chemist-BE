package dthaibinhf.project.chemistbe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.AcademicYear}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class AcademicYearDTO extends BaseDTO implements Serializable {
    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{4}", message = "Year must be in format YYYY-YYYY")
    String year;
}