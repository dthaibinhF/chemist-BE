package dthaibinhf.project.chemistbe.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Exam}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class ExamDTO extends BaseDTO implements Serializable {
    String name;
    String description;
    String type;
    OffsetDateTime testDate;
    Set<ScoreDTO> scores;
}