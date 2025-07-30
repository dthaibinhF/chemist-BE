package dthaibinhf.project.chemistbe.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIAgentService {

    private final ChatClient chatClient;
    private final AtomicInteger requestCounter = new AtomicInteger(0);
    // Rate limiting: Max 10 requests per minute to avoid Anthropic rate limits
    private static final int MAX_REQUESTS_PER_MINUTE = 10;
    private volatile long lastResetTime = System.currentTimeMillis();

    /**
     * Process a user query and get AI response with conversation context
     * 
     * @param userQuery The user's natural language query
     * @param conversationId Unique conversation ID for memory context
     * @return AI response as string
     */
    public String processQuery(String userQuery, String conversationId, String userRole) {
        if (!checkRateLimit()) {
            log.warn("Rate limit exceeded for conversation {}", conversationId);
            return "Xin lỗi, hệ thống đang quá tải. Vui lòng thử lại sau ít phút ạ.";
        }

        try {
            log.info("Processing AI query for conversation {} with role {}: {}", conversationId, userRole, userQuery);
            
            String roleBasedSystemMessage = buildRoleBasedSystemMessage(userRole);
            
            return executeWithRetry(() -> {
                return chatClient.prompt()
                        .system(roleBasedSystemMessage)
                        .user(userQuery)
                        .advisors(advisorSpec -> advisorSpec.param("conversationId", conversationId))
                        .call()
                        .content();
            }, conversationId);
                    
        } catch (Exception e) {
            log.error("Error processing AI query for conversation {}: {}", conversationId, e.getMessage(), e);
            return handleAIError(e, userRole);
        }
    }

    /**
     * Process a user query without conversation context (stateless)
     * 
     * @param userQuery The user's natural language query
     * @return AI response as string
     */
    public String processQuery(String userQuery, String userRole) {
        if (!checkRateLimit()) {
            log.warn("Rate limit exceeded for stateless query");
            return "Xin lỗi, hệ thống đang quá tải. Vui lòng thử lại sau ít phút ạ.";
        }

        try {
            log.info("Processing stateless AI query with role {}: {}", userRole, userQuery);
            
            String roleBasedSystemMessage = buildRoleBasedSystemMessage(userRole);
            
            return executeWithRetry(() -> {
                return chatClient.prompt()
                        .system(roleBasedSystemMessage)
                        .user(userQuery)
                        .call()
                        .content();
            }, "stateless");
                    
        } catch (Exception e) {
            log.error("Error processing stateless AI query: {}", e.getMessage(), e);
            return handleAIError(e, userRole);
        }
    }

    /**
     * Process a user query with streaming response for real-time updates
     * WORKAROUND: Due to Spring AI 1.0.0-SNAPSHOT streaming issues with Anthropic,
     * we fall back to non-streaming with simulated chunking
     * 
     * @param userQuery The user's natural language query
     * @param conversationId Unique conversation ID for memory context
     * @return Flux of response chunks for streaming
     */
    public Flux<String> streamQuery(String userQuery, String conversationId, String userRole) {
        if (!checkRateLimit()) {
            log.warn("Rate limit exceeded for streaming query: {}", conversationId);
            return Flux.just("Xin lỗi, hệ thống đang quá tải. Vui lòng thử lại sau ít phút ạ.");
        }

        try {
            log.info("Processing streaming AI query for conversation {} with role {}: {}", conversationId, userRole, userQuery);
            log.warn("Using fallback streaming due to Spring AI Anthropic streaming issues");
            
            // Fallback: Get complete response and simulate streaming
            String fullResponse = processQuery(userQuery, conversationId, userRole);
            
            // Simulate streaming by chunking the response
            return simulateStreaming(fullResponse);
                    
        } catch (Exception e) {
            log.error("Error processing streaming AI query for conversation {}: {}", conversationId, e.getMessage(), e);
            return Flux.just(handleAIError(e, userRole));
        }
    }


    /**
     * Process a query with custom system message
     * 
     * @param userQuery The user's query
     * @param systemMessage Custom system instruction
     * @param conversationId Conversation ID for context
     * @return AI response
     */
    public String processQueryWithContext(String userQuery, String systemMessage, String conversationId, String userRole) {
        if (!checkRateLimit()) {
            log.warn("Rate limit exceeded for context query: {}", conversationId);
            return "Xin lỗi, hệ thống đang quá tải. Vui lòng thử lại sau ít phút ạ.";
        }

        try {
            log.info("Processing AI query with custom system message for conversation {} with role {}", conversationId, userRole);
            
            // Combine custom system message with role-based restrictions
            String combinedSystemMessage = systemMessage + "\n\n" + buildRoleBasedSystemMessage(userRole);
            
            return executeWithRetry(() -> {
                return chatClient.prompt()
                        .system(combinedSystemMessage)
                        .user(userQuery)
                        .advisors(advisorSpec -> advisorSpec.param("conversationId", conversationId))
                        .call()
                        .content();
            }, conversationId);
                    
        } catch (Exception e) {
            log.error("Error processing AI query with context for conversation {}: {}", conversationId, e.getMessage(), e);
            return handleAIError(e, userRole);
        }
    }

    private String buildRoleBasedSystemMessage(String userRole) {
        String baseMessage = """
            Bạn là trợ lý giáo dục thông minh cho hệ thống quản lý trường học. Bạn hỗ trợ người dùng truy cập thông tin về
            học sinh, lớp học, lịch học và học phí.
            
            Cách trả lời của bạn:
            - Luôn trả lời bằng tiếng Việt một cách tự nhiên và thân thiện
            - Giữ câu trả lời ngắn gọn, dễ hiểu, không dài dòng
            - Sử dụng ngôn ngữ đời thường, không quá trang trọng
            - Thêm "ạ", "nhé", "dạ" để tạo cảm giác thân thiện
            - Chỉ cung cấp thông tin chính xác từ dữ liệu có sẵn
            """;

        return switch (userRole.toUpperCase()) {
            case "PUBLIC" -> baseMessage + """
                
                QUYỀN TRUY CẬP CỦA BẠN (Người dùng công khai):
                ✅ Thông tin học phí cơ bản (số tiền, tên học phí, thời gian)
                ✅ Lịch học chung (giờ học, phòng học, môn học)
                ✅ Thông tin chung về trường học
                ❌ KHÔNG được cung cấp thông tin cá nhân học sinh
                ❌ KHÔNG được cung cấp chi tiết thanh toán cá nhân
                ❌ KHÔNG được cung cấp thông tin điểm số, điện thoại, địa chỉ
                
                Nếu được hỏi thông tin riêng tư, hãy lịch sự từ chối và đề xuất đăng nhập.
                """;
                
            case "STUDENT", "PARENT" -> baseMessage + """
                
                QUYỀN TRUY CẬP CỦA BẠN (Học sinh/Phụ huynh):
                ✅ Thông tin của bản thân hoặc con em
                ✅ Học phí và thanh toán của mình
                ✅ Lịch học và điểm số của mình
                ❌ KHÔNG được xem thông tin của học sinh khác
                """;
                
            case "TEACHER" -> baseMessage + """
                
                QUYỀN TRUY CẬP CỦA BẠN (Giáo viên):
                ✅ Thông tin học sinh trong lớp mình dạy
                ✅ Lịch học của các lớp mình phụ trách
                ✅ Thông tin học phí cơ bản
                ❌ KHÔNG được xem thông tin học sinh lớp khác
                """;
                
            case "MANAGER" -> baseMessage + """
                
                QUYỀN TRUY CẬP CỦA BẠN (Quản lý):
                ✅ Tất cả thông tin lớp học và lịch học
                ✅ Tất cả thông tin học phí và thanh toán
                ✅ Thông tin hành chính
                ❌ Hạn chế thông tin cá nhân nhạy cảm của học sinh
                """;
                
            case "ADMIN" -> baseMessage + """
                
                QUYỀN TRUY CẬP CỦA BẠN (Quản trị viên):
                ✅ Toàn quyền truy cập tất cả thông tin trong hệ thống
                ✅ Thông tin chi tiết về học sinh, học phí, lịch học
                ✅ Báo cáo và thống kê đầy đủ
                """;
                
            default -> baseMessage + """
                
                QUYỀN TRUY CẬP MẶC ĐỊNH:
                ✅ Chỉ thông tin công khai cơ bản
                ❌ Không có quyền truy cập thông tin riêng tư
                """;
        };
    }

    /**
     * Check rate limit to prevent API overload
     */
    private boolean checkRateLimit() {
        long currentTime = System.currentTimeMillis();
        
        // Reset counter every minute
        if (currentTime - lastResetTime > 60000) {
            requestCounter.set(0);
            lastResetTime = currentTime;
        }
        
        int currentRequests = requestCounter.incrementAndGet();
        return currentRequests <= MAX_REQUESTS_PER_MINUTE;
    }

    /**
     * Execute AI request with retry logic
     */
    private String executeWithRetry(java.util.function.Supplier<String> operation, String context) {
        int maxRetries = 3;
        int currentAttempt = 0;
        
        while (currentAttempt < maxRetries) {
            try {
                return operation.get();
            } catch (Exception e) {
                currentAttempt++;
                log.warn("AI request attempt {} failed for context {}: {}", currentAttempt, context, e.getMessage());
                
                if (currentAttempt >= maxRetries) {
                    throw e; // Re-throw on final attempt
                }
                
                // Exponential backoff: wait 1s, 2s, 4s
                try {
                    Thread.sleep(1000L * (1L << (currentAttempt - 1)));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Request interrupted", ie);
                }
            }
        }
        
        return "Xin lỗi, tôi gặp lỗi khi xử lý yêu cầu của bạn. Vui lòng thử lại ạ.";
    }

    /**
     * Handle different types of AI errors with appropriate Vietnamese messages
     */
    private String handleAIError(Exception e, String userRole) {
        String errorMessage = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        
        if (errorMessage.contains("overloaded") || errorMessage.contains("rate limit")) {
            log.warn("Anthropic API overloaded or rate limited for role {}", userRole);
            return "Xin lỗi, hệ thống AI hiện đang quá tải. Vui lòng thử lại sau vài phút ạ.";
        } else if (errorMessage.contains("timeout")) {
            log.warn("Anthropic API timeout for role {}", userRole);
            return "Xin lỗi, yêu cầu của bạn mất quá nhiều thời gian. Vui lòng thử lại với câu hỏi ngắn gọn hơn ạ.";
        } else if (errorMessage.contains("unauthorized") || errorMessage.contains("forbidden")) {
            log.error("Anthropic API authentication error for role {}", userRole);
            return "Xin lỗi, có lỗi xác thực hệ thống. Vui lòng liên hệ quản trị viên ạ.";
        } else if (errorMessage.contains("nullpointerexception") || errorMessage.contains("messageaggregator")) {
            log.error("Spring AI MessageAggregator error for role {}: {}", userRole, e.getMessage());
            return "Xin lỗi, có lỗi kỹ thuật trong hệ thống AI. Vui lòng thử lại hoặc liên hệ hỗ trợ ạ.";
        } else {
            log.error("Unexpected AI error for role {}: {}", userRole, e.getMessage(), e);
            return "Xin lỗi, tôi gặp lỗi khi xử lý yêu cầu của bạn. Vui lòng thử lại ạ.";
        }
    }

    /**
     * Simulate streaming by chunking a complete response
     * This is a workaround for Spring AI Anthropic streaming issues
     */
    private Flux<String> simulateStreaming(String fullResponse) {
        if (fullResponse == null || fullResponse.trim().isEmpty()) {
            return Flux.just("Xin lỗi, tôi không thể tạo ra câu trả lời cho yêu cầu này ạ.");
        }

        // Split response into words for word-by-word streaming
        String[] words = fullResponse.split("\\s+");
        
        return Flux.fromArray(words)
                .delayElements(Duration.ofMillis(50)) // 50ms delay between words
                .map(word -> word + " "); // Add space back
    }
}