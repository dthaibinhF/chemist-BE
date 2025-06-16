package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dthaibinhf.project.chemistbe.model.Fee;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * DTO for {@link Fee}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class FeeDTO extends BaseDTO implements Serializable {
    String name;
    String description;
    BigDecimal amount;
    @JsonProperty("start_time")
    OffsetDateTime startTime;
    @JsonProperty("end_time")
    OffsetDateTime endTime;
}