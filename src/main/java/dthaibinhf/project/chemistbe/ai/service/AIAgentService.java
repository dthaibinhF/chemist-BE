package dthaibinhf.project.chemistbe.ai.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIAgentService {

    private final ChatClient chatClient;

    /**
     * Process a user query and get AI response with conversation context
     * 
     * @param userQuery The user's natural language query
     * @param conversationId Unique conversation ID for memory context
     * @return AI response as string
     */
    public String processQuery(String userQuery, String conversationId, String userRole) {
        try {
            log.info("Processing AI query for conversation {} with role {}: {}", conversationId, userRole, userQuery);
            
            String roleBasedSystemMessage = buildRoleBasedSystemMessage(userRole);
            
            return chatClient.prompt()
                    .system(roleBasedSystemMessage)
                    .user(userQuery)
                    .advisors(advisorSpec -> advisorSpec.param("conversationId", conversationId))
                    .call()
                    .content();
                    
        } catch (Exception e) {
            log.error("Error processing AI query for conversation {}: {}", conversationId, e.getMessage(), e);
            return "Xin lỗi, tôi gặp lỗi khi xử lý yêu cầu của bạn. Vui lòng thử lại ạ.";
        }
    }

    /**
     * Process a user query without conversation context (stateless)
     * 
     * @param userQuery The user's natural language query
     * @return AI response as string
     */
    public String processQuery(String userQuery, String userRole) {
        try {
            log.info("Processing stateless AI query with role {}: {}", userRole, userQuery);
            
            String roleBasedSystemMessage = buildRoleBasedSystemMessage(userRole);
            
            return chatClient.prompt()
                    .system(roleBasedSystemMessage)
                    .user(userQuery)
                    .call()
                    .content();
                    
        } catch (Exception e) {
            log.error("Error processing stateless AI query: {}", e.getMessage(), e);
            return "Xin lỗi, tôi gặp lỗi khi xử lý yêu cầu của bạn. Vui lòng thử lại ạ.";
        }
    }

    /**
     * Process a user query with streaming response for real-time updates
     * 
     * @param userQuery The user's natural language query
     * @param conversationId Unique conversation ID for memory context
     * @return Flux of response chunks for streaming
     */
    public Flux<String> streamQuery(String userQuery, String conversationId, String userRole) {
        try {
            log.info("Processing streaming AI query for conversation {} with role {}: {}", conversationId, userRole, userQuery);
            
            String roleBasedSystemMessage = buildRoleBasedSystemMessage(userRole);
            
            return chatClient.prompt()
                    .system(roleBasedSystemMessage)
                    .user(userQuery)
                    .advisors(advisorSpec -> advisorSpec.param("conversationId", conversationId))
                    .stream()
                    .content();
                    
        } catch (Exception e) {
            log.error("Error processing streaming AI query for conversation {}: {}", conversationId, e.getMessage(), e);
            return Flux.just("Xin lỗi, tôi gặp lỗi khi xử lý yêu cầu của bạn. Vui lòng thử lại ạ.");
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
        try {
            log.info("Processing AI query with custom system message for conversation {} with role {}", conversationId, userRole);
            
            // Combine custom system message with role-based restrictions
            String combinedSystemMessage = systemMessage + "\n\n" + buildRoleBasedSystemMessage(userRole);
            
            return chatClient.prompt()
                    .system(combinedSystemMessage)
                    .user(userQuery)
                    .advisors(advisorSpec -> advisorSpec.param("conversationId", conversationId))
                    .call()
                    .content();
                    
        } catch (Exception e) {
            log.error("Error processing AI query with context for conversation {}: {}", conversationId, e.getMessage(), e);
            return "Xin lỗi, tôi gặp lỗi khi xử lý yêu cầu của bạn. Vui lòng thử lại ạ.";
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
}