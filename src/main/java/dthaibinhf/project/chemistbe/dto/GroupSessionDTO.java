package dthaibinhf.project.chemistbe.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.GroupSession}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class GroupSessionDTO extends BaseDTO implements Serializable {
    String sessionType;
    LocalDate date;
    OffsetDateTime startTime;
    OffsetDateTime endTime;
    Set<Integer> groupIds;
}