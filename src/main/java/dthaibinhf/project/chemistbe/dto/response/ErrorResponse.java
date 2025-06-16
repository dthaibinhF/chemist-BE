package dthaibinhf.project.chemistbe.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@JsonPropertyOrder({ "timestamp", "status", "error", "data" })
@Getter
@Setter
public class ErrorResponse {
    private ZonedDateTime timestamp;
    private int status;
    private String error;
    private String path;


    public ErrorResponse(int status, String error, String path) {
        this.timestamp = ZonedDateTime.now();
        this.status = status;
        this.error = error;
        this.path = path;
    }
}

