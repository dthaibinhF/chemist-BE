package dthaibinhf.project.chemistbe.dto;

import dthaibinhf.project.chemistbe.model.Grade;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link Grade}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class GradeDTO extends BaseDTO implements Serializable {
    String name;
}