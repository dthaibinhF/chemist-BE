package dthaibinhf.project.chemistbe.dto;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Fee}
 */
@Value
public class FeeBasicDto implements Serializable {
    Integer id;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
    OffsetDateTime endAt;
    OffsetDateTime startTime;
    OffsetDateTime endTime;
    String name;
    String description;
    BigDecimal amount;
    Set<GroupDto> groups;

    /**
     * DTO for {@link dthaibinhf.project.chemistbe.model.Group}
     */
    @Value
    public static class GroupDto implements Serializable {
        Integer id;
        OffsetDateTime createdAt;
        OffsetDateTime updatedAt;
        OffsetDateTime endAt;
        String name;
        String level;
    }
}