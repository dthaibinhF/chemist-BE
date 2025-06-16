package dthaibinhf.project.chemistbe.dto.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@JsonPropertyOrder({ "timestamp", "status", "message", "payload" })
@Getter
@Setter
public class ApiResponse<T> {
    private ZonedDateTime timestamp;
    private int status;
    private String message;
    private T payload;

    public ApiResponse(int status, String message, T payload) {
        this.timestamp = ZonedDateTime.now();
        this.status = status;
        this.message = message;
        this.payload = payload;
    }
}

