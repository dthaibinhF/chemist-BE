package dthaibinhf.project.chemistbe.dto;

import dthaibinhf.project.chemistbe.model.SchoolClass;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link SchoolClass}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class SchoolClassDTO extends BaseDTO implements Serializable {
    String name;
}