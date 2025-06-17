package dthaibinhf.project.chemistbe.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Room}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class RoomDTO extends BaseDTO implements Serializable {
    String name;
    String location;
    Integer capacity;
}