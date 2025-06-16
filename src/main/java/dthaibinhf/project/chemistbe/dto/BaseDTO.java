package dthaibinhf.project.chemistbe.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
public abstract class BaseDTO {
    @JsonProperty("id")
    Integer id;

    @JsonProperty("create_at")
    OffsetDateTime createdAt;

    @JsonProperty("update_at")
    OffsetDateTime updatedAt;

    @JsonProperty("end_at")
    OffsetDateTime endAt;
}