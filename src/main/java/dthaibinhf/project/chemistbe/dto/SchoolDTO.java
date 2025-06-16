package dthaibinhf.project.chemistbe.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.School}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class SchoolDTO extends BaseDTO implements Serializable {
    String name;
}