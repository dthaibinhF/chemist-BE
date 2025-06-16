package dthaibinhf.project.chemistbe.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link dthaibinhf.project.chemistbe.model.Score}
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class ScoreDTO extends BaseDTO implements Serializable {
    Integer examId;
    String examName;
    Integer studentId;
    String studentName;
    Integer score;
    String description;
}