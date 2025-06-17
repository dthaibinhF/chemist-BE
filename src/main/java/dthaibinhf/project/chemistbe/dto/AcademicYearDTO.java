package dthaibinhf.project.chemistbe.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.AcademicYear}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class AcademicYearDTO extends BaseDTO implements Serializable {
    String year;
}