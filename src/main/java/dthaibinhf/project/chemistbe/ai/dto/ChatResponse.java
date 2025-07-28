package dthaibinhf.project.chemistbe.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    @JsonProperty("response")
    private String response;

    @JsonProperty("conversation_id")
    private String conversationId;

    @JsonProperty("timestamp")
    @Builder.Default
    private OffsetDateTime timestamp = OffsetDateTime.now();

    @JsonProperty("tools_used")
    private String[] toolsUsed;

    @JsonProperty("error")
    private String error;

    @JsonProperty("success")
    @Builder.Default
    private Boolean success = true;
}