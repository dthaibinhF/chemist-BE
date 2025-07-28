package dthaibinhf.project.chemistbe.ai.controller;

import dthaibinhf.project.chemistbe.ai.dto.ChatRequest;
import dthaibinhf.project.chemistbe.ai.dto.ChatResponse;
import dthaibinhf.project.chemistbe.ai.service.AIAgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Agent", description = "AI-powered educational assistant")
public class AIController {

    private final AIAgentService aiAgentService;

    @PostMapping("/chat")
    @Operation(summary = "Chat with AI assistant", 
               description = "Send a message to the AI assistant and get a response. Supports conversation context and tool calling.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful AI response"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        try {
            log.info("Received chat request: conversationId={}, message length={}", 
                    request.getConversationId(), request.getMessage().length());

            String conversationId = request.getConversationId() != null ? 
                    request.getConversationId() : generateConversationId();

            String response;
            if (request.getSystemMessage() != null && !request.getSystemMessage().trim().isEmpty()) {
                response = aiAgentService.processQueryWithContext(
                        request.getMessage(), 
                        request.getSystemMessage(), 
                        conversationId
                );
            } else {
                response = aiAgentService.processQuery(request.getMessage(), conversationId);
            }

            ChatResponse chatResponse = ChatResponse.builder()
                    .response(response)
                    .conversationId(conversationId)
                    .success(true)
                    .build();

            return ResponseEntity.ok(chatResponse);

        } catch (Exception e) {
            log.error("Error processing chat request", e);
            ChatResponse errorResponse = ChatResponse.builder()
                    .error("Failed to process your request: " + e.getMessage())
                    .success(false)
                    .build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Stream chat with AI assistant", 
               description = "Stream AI responses in real-time using Server-Sent Events (SSE)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful streaming response"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters")
    })
    public SseEmitter streamChat(
            @Parameter(description = "User message", required = true)
            @RequestParam("message") String message,
            
            @Parameter(description = "Conversation ID for context")
            @RequestParam(value = "conversation_id", required = false) String conversationId,
            
            @Parameter(description = "Custom system message")
            @RequestParam(value = "system_message", required = false) String systemMessage) {

        log.info("Received streaming chat request: conversationId={}, message length={}", 
                conversationId, message.length());

        SseEmitter emitter = new SseEmitter(30000L); // 30-second timeout
        String finalConversationId = conversationId != null ? conversationId : generateConversationId();

        CompletableFuture.runAsync(() -> {
            try {
                Flux<String> responseStream;
                if (systemMessage != null && !systemMessage.trim().isEmpty()) {
                    // For custom system message, we'll use the non-streaming method and send as a single event
                    String response = aiAgentService.processQueryWithContext(message, systemMessage, finalConversationId);
                    emitter.send(SseEmitter.event().data(response));
                } else {
                    responseStream = aiAgentService.streamQuery(message, finalConversationId);
                    responseStream
                            .timeout(Duration.ofSeconds(25))
                            .doOnNext(chunk -> {
                                try {
                                    emitter.send(SseEmitter.event().data(chunk));
                                } catch (IOException e) {
                                    log.error("Error sending SSE chunk", e);
                                    emitter.completeWithError(e);
                                }
                            })
                            .doOnComplete(() -> {
                                try {
                                    emitter.send(SseEmitter.event().name("end").data("[END]"));
                                    emitter.complete();
                                } catch (IOException e) {
                                    log.error("Error completing SSE", e);
                                    emitter.completeWithError(e);
                                }
                            })
                            .doOnError(error -> {
                                log.error("Error in streaming response", error);
                                try {
                                    emitter.send(SseEmitter.event().name("error")
                                            .data("Error occurred while processing your request"));
                                } catch (IOException e) {
                                    log.error("Error sending error event", e);
                                }
                                emitter.completeWithError(error);
                            })
                            .subscribe();
                }
            } catch (Exception e) {
                log.error("Error in streaming chat", e);
                emitter.completeWithError(e);
            }
        });

        emitter.onCompletion(() -> log.info("SSE completed for conversation: {}", finalConversationId));
        emitter.onTimeout(() -> log.warn("SSE timeout for conversation: {}", finalConversationId));
        emitter.onError(throwable -> log.error("SSE error for conversation: {}", finalConversationId, throwable));

        return emitter;
    }

    @PostMapping("/chat/simple")
    @Operation(summary = "Simple chat without conversation context", 
               description = "Send a stateless message to the AI assistant")
    public ResponseEntity<ChatResponse> simpleChat(@Valid @RequestBody ChatRequest request) {
        try {
            log.info("Received simple chat request: message length={}", request.getMessage().length());

            String response = aiAgentService.processQuery(request.getMessage());

            ChatResponse chatResponse = ChatResponse.builder()
                    .response(response)
                    .success(true)
                    .build();

            return ResponseEntity.ok(chatResponse);

        } catch (Exception e) {
            log.error("Error processing simple chat request", e);
            ChatResponse errorResponse = ChatResponse.builder()
                    .error("Failed to process your request: " + e.getMessage())
                    .success(false)
                    .build();
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/health")
    @Operation(summary = "AI service health check", description = "Check if AI service is available")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("AI service is running");
    }

    private String generateConversationId() {
        return "conv_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}