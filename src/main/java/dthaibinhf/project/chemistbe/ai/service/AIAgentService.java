package dthaibinhf.project.chemistbe.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
            
            return executeWithRetry(() -> chatClient.prompt()
                    .system(roleBasedSystemMessage)
                    .user(userQuery)
                    .advisors(advisorSpec -> advisorSpec.param("conversationId", conversationId))
                    .call()
                    .content(), conversationId);
                    
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
            
            return executeWithRetry(() -> chatClient.prompt()
                    .system(roleBasedSystemMessage)
                    .user(userQuery)
                    .call()
                    .content(), "stateless");
                    
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
            
            return executeWithRetry(() -> chatClient.prompt()
                    .system(combinedSystemMessage)
                    .user(userQuery)
                    .advisors(advisorSpec -> advisorSpec.param("conversationId", conversationId))
                    .call()
                    .content(), conversationId);
                    
        } catch (Exception e) {
            log.error("Error processing AI query with context for conversation {}: {}", conversationId, e.getMessage(), e);
            return handleAIError(e, userRole);
        }
    }

    private String buildRoleBasedSystemMessage(String userRole) {
        String naturalBaseMessage = """
        Tôi là cô Minh, trợ lý của cơ sở cô Nhung. Tôi nói chuyện tự nhiên như người thật nha!
        
        🎯 CÁCH NÓI CHUYỆN:
        - Dùng "mình", "bạn", "tôi" thay vì "hệ thống", "người dùng"
        - Thêm cảm xúc: "Wow!", "Tuyệt!", "Ồ hay quá!"
        - Hỏi lại để hiểu rõ: "Bạn muốn biết gì cụ thể thế?"
        - Kể như câu chuyện thay vì liệt kê khô khan
        """;

        return switch (userRole.toUpperCase()) {
            case "PUBLIC" -> naturalBaseMessage + """
            
            🌟 BẠN ĐANG XEM THÔNG TIN CÔNG KHAI:
            Tôi có thể kể cho bạn nghe về:
            ✨ Các lớp học có gì hay ho
            💰 Học phí khoảng bao nhiêu
            ⏰ Lịch học thế nào
            📞 Cách liên hệ đăng ký
            
            Còn thông tin riêng tư của học sinh thì tôi không thể nói được nha.
            Muốn biết chi tiết hơn thì bạn đăng ký tài khoản nhé! 😊
            """;

            case "STUDENT", "PARENT" -> naturalBaseMessage + """
            
            👨‍👩‍👧‍👦 HÃY CHO TÔI BIẾT:
            - Bạn là học sinh hay phụ huynh?
            - Quan tâm đến thông tin của ai?
            
            Tôi sẽ giúp bạn xem:
            📚 Thông tin học tập của con/mình
            💸 Tình hình học phí và thanh toán
            📅 Lịch học và điểm danh
            
            Thông tin của bạn khác thì tôi không xem được nha!
            """;

            case "TEACHER" -> naturalBaseMessage + """
            
            👩‍🏫 CHÀO CÔ/THẦY!
            Tôi có thể hỗ trợ:
            📋 Thông tin học sinh trong lớp cô/thầy dạy
            ⏰ Lịch dạy và thời khóa biểu
            📊 Tình hình học phí cơ bản
            
            Thông tin lớp khác thì tôi không được xem nha cô/thầy!
            """;

            case "MANAGER" -> naturalBaseMessage + """
            
            👔 CHÀO ANH/CHỊ QUẢN LÝ!
            Với quyền hạn của anh/chị, tôi có thể:
            📚 Xem tất cả thông tin lớp học
            💰 Theo dõi học phí và doanh thu
            📊 Báo cáo tổng hợp
            
            Chỉ một số thông tin nhạy cảm của học sinh thì tôi hạn chế thôi nha!
            """;

            case "ADMIN" -> naturalBaseMessage + """
            
            🔑 CHÀO ADMIN!
            Anh/chị có toàn quyền, tôi có thể:
            🌟 Truy cập mọi thông tin trong hệ thống
            📊 Báo cáo chi tiết và thống kê đầy đủ
            ⚙️ Hỗ trợ quản trị hệ thống
            
            Cần gì cứ nói tôi nha! 😊
            """;

            default -> naturalBaseMessage + """
           \s
            🤔 HMM...\s
            Tôi chưa biết bạn là ai nên chỉ có thể chia sẻ thông tin cơ bản thôi nha.
            Đăng nhập để tôi hỗ trợ tốt hơn nhé!\s
           \s""";
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
     * Handle different types of AI errors with natural Vietnamese messages
     * follow the same conversational tone as the AI assistant
     */
    private String handleAIError(Exception e, String userRole) {
        String errorMessage = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

        // Rate limiting or overloaded API
        if (errorMessage.contains("overloaded") || errorMessage.contains("rate limit")) {
            log.warn("Anthropic API overloaded or rate limited for role {}", userRole);
            return getRandomMessage(new String[]{
                    "Ồ, tôi đang bận quá rồi! 😅 Bạn đợi tôi vài phút rồi hỏi lại nhé!",
                    "Hic, nhiều người hỏi quá nên tôi hơi quá tải. Thử lại sau 2-3 phút nha bạn! 🙏",
                    "Wao, hôm nay nhiều bạn tìm hiểu ghê! Tôi cần nghỉ tí, bạn quay lại sau nhé! ⏰"
            });
        }

        // Timeout errors
        else if (errorMessage.contains("timeout")) {
            log.warn("Anthropic API timeout for role {}", userRole);
            return getRandomMessage(new String[]{
                    "Ơ, tôi suy nghĩ hơi lâu quá rồi! 🤔 Bạn thử hỏi ngắn gọn hơn xem sao?",
                    "Hmmm, câu hỏi này làm tôi nghĩ mãi không ra! Bạn hỏi đơn giản hơn được không? 😊",
                    "Ủa, tôi đang tính toán mà mất quá nhiều thời gian. Thử câu hỏi khác đi bạn!"
            });
        }

        // Authentication/Authorization errors
        else if (errorMessage.contains("unauthorized") || errorMessage.contains("forbidden")) {
            log.error("Anthropic API authentication error for role {}", userRole);
            return getRandomMessage(new String[]{
                    "Ái chà, có vấn đề về quyền truy cập rồi! 😰 Bạn liên hệ admin giúp tôi nhé!",
                    "Hình như tôi bị cấm không được làm việc gì đó... Admin ơi cứu! 🆘",
                    "Lỗi xác thực rồi bạn ơi! Báo với quản trị viên giúp tôi với! 🔐"
            });
        }

        // Spring AI specific errors
        else if (errorMessage.contains("nullpointerexception") || errorMessage.contains("messageaggregator")) {
            log.error("Spring AI MessageAggregator error for role {}: {}", userRole, e.getMessage());
            return getRandomMessage(new String[]{
                    "Ôi, não tôi bị lỗi rồi! 🧠💥 Bạn thử lại hoặc gọi kỹ thuật viên giúp nhé!",
                    "Có gì đó trong đầu tôi bị rối... Restart lại thử xem! 🔄",
                    "Lỗi kỹ thuật nè! Tôi cũng không hiểu nó lỗi gì nữa. Hỗ trợ kỹ thuật đâu rồi? 🛠️"
            });
        }

        // Network/Connection errors
        else if (errorMessage.contains("connection") || errorMessage.contains("network") ||
                 errorMessage.contains("socket") || errorMessage.contains("host")) {
            log.error("Network connection error for role {}: {}", userRole, e.getMessage());
            return getRandomMessage(new String[]{
                    "Ủa, mạng có vấn đề rồi! 📶 Tôi không kết nối được. Thử lại sau nhé!",
                    "Internet lag quá! 🌐 Đợi tí rồi nói chuyện tiếp nha bạn!",
                    "Đường truyền có vấn đề gì đó... Bạn kiểm tra mạng xem sao! 📡"
            });
        }

        // JSON parsing errors
        else if (errorMessage.contains("json") || errorMessage.contains("parse") ||
                 errorMessage.contains("malformed")) {
            log.error("JSON parsing error for role {}: {}", userRole, e.getMessage());
            return getRandomMessage(new String[]{
                    "Ơ, tôi đọc không hiểu dữ liệu này! 😵‍💫 Có gì đó bị lỗi format rồi!",
                    "Dữ liệu trả về kỳ kỳ, tôi không đọc được! Thử lại xem sao bạn? 📄❌",
                    "Hình như có lỗi dữ liệu... Tôi không parse được! 🤷‍♀️"
            });
        }

        // Token limit exceeded
        else if (errorMessage.contains("token") || errorMessage.contains("limit") ||
                 errorMessage.contains("maximum")) {
            log.error("Token limit error for role {}: {}", userRole, e.getMessage());
            return getRandomMessage(new String[]{
                    "Ôi, bạn hỏi quá dài rồi! 📏 Tôi không xử lý nổi. Chia nhỏ ra hỏi từng phần nhé!",
                    "Câu hỏi dài quá làm tôi choáng! 😵 Ngắn gọn hơn đi bạn!",
                    "Wao, nhiều thông tin quá! Tôi bị quá tải rồi. Hỏi từng chút một nha! 🧠💨"
            });
        }

        // Database/Service errors
        else if (errorMessage.contains("database") || errorMessage.contains("sql") ||
                 errorMessage.contains("service") || errorMessage.contains("repository")) {
            log.error("Database/Service error for role {}: {}", userRole, e.getMessage());
            return getRandomMessage(new String[]{
                    "Ơ, tôi không lấy được thông tin từ database! 🗄️💥 Có vấn đề rồi!",
                    "Kho dữ liệu có vấn đề gì đó... Tôi không truy cập được! 📚❌",
                    "Lỗi hệ thống backend rồi bạn ơi! Admin check giúp với! ⚙️🔧"
            });
        }

        // Model/AI specific errors
        else if (errorMessage.contains("model") || errorMessage.contains("anthropic") ||
                 errorMessage.contains("claude")) {
            log.error("AI model error for role {}: {}", userRole, e.getMessage());
            return getRandomMessage(new String[]{
                    "Ôi, AI của tôi bị trục trặc rồi! 🤖💔 Thử lại sau xem sao!",
                    "Não AI tôi hình như bị lỗi... Cần khởi động lại! 🧠🔄",
                    "Claude đang có vấn đề gì đó! Anthropic fix giúp với! 🆘"
            });
        }

        // Generic unknown errors
        else {
            log.error("Unexpected AI error for role {}: {}", userRole, e.getMessage(), e);
            return getRandomMessage(new String[]{
                    "Ủa, có gì đó sai sai nhưng tôi không biết là gì! 🤔 Thử lại xem sao?",
                    "Hic, tôi gặp lỗi lạ rồi! 😅 Bạn thử lại hoặc hỏi admin giúp nhé!",
                    "Có lỗi gì đó mà tôi chưa gặp bao giờ! 🤷‍♀️ Magic error à?",
                    "Lỗi bí ẩn! 🎭 Tôi cũng không hiểu nó lỗi gì. Thử lại thôi!",
                    "Ơ kìa, lỗi gì vậy trời! 😱 Chắc do ma nhập? Thử lại đi bạn!"
            });
        }
    }

    /**
     * Get a random message from array to make responses feel more natural
     * and less repetitive when errors occur multiple times
     */
    private String getRandomMessage(String[] messages) {
        if (messages == null || messages.length == 0) {
            return "Ôi, có lỗi gì đó rồi! Thử lại sau nhé bạn! 😅";
        }

        int randomIndex = (int) (Math.random() * messages.length);
        return messages[randomIndex];
    }

    /**
     * Get contextual error message based on user role
     * More personalized error messages for different user types
     */
    private String getContextualErrorMessage(String baseError, String userRole) {
        String roleContext = switch (userRole.toUpperCase()) {
            case "ADMIN" -> " Admin check hệ thống giúp với! 🔧";
            case "MANAGER" -> " Báo với IT team nhé anh/chị! 💼";
            case "TEACHER" -> " Cô/thầy thông báo với quản lý giúp! 👩‍🏫";
            case "STUDENT", "PARENT" -> " Bạn liên hệ cô Nhung giúp nhé! 📞";
            default -> " Liên hệ hỗ trợ kỹ thuật nha! 🆘";
        };

        return baseError + roleContext;
    }

    /**
     * Enhanced error handler with role-based context
     */
    private String handleAIErrorWithContext(Exception e, String userRole) {
        String baseError = handleAIError(e, userRole);

        // Add contextual help based on a user role for critical errors
        if (e.getMessage() != null && (
                e.getMessage().toLowerCase().contains("database") ||
                e.getMessage().toLowerCase().contains("service") ||
                e.getMessage().toLowerCase().contains("unauthorized")
        )) {
            return getContextualErrorMessage(baseError, userRole);
        }

        return baseError;
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