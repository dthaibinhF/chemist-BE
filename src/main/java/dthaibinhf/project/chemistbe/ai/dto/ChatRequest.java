package dthaibinhf.project.chemistbe.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    @NotBlank(message = "Message cannot be blank")
    @Size(max = 5000, message = "Message cannot exceed 5000 characters")
    @JsonProperty("message")
    private String message;

    @JsonProperty("conversation_id")
    private String conversationId;

    @JsonProperty("system_message")
    private String systemMessage;

    @JsonProperty("stream")
    @Builder.Default
    private Boolean stream = false;
}