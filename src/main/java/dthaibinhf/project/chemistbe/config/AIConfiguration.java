package dthaibinhf.project.chemistbe.config;

import dthaibinhf.project.chemistbe.service.FeeService;
import dthaibinhf.project.chemistbe.service.GroupService;
import dthaibinhf.project.chemistbe.service.PaymentDetailService;
import dthaibinhf.project.chemistbe.service.StudentPaymentService;
import dthaibinhf.project.chemistbe.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.anthropic.api.AnthropicApi;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AIConfiguration {

    @Bean
    public ChatMemory chatMemory() {
        log.info("Configuring in-memory chat memory for AI conversations with reduced window size");
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(10) // Reduced from 20 to 10 for better performance and lower token usage
                .build();
    }

    @Bean
    public ChatClient chatClient(AnthropicChatModel chatModel,
                                 ChatMemory chatMemory,
                                 StudentService studentService,
                                 GroupService groupService,
                                 FeeService feeService,
                                 StudentPaymentService studentPaymentService,
                                 PaymentDetailService paymentDetailService) {
        log.info("Configuring ChatClient with Anthropic Claude model, memory advisor, and AI tools");

        String systemPrompt = """
            Bạn là trợ lý giáo dục thông minh cho hệ thống quản lý trường học. Bạn hỗ trợ người dùng truy cập thông tin về 
            học sinh, lớp học, lịch học và học phí.
            
            Cách trả lời của bạn:
            - Luôn trả lời bằng tiếng Việt một cách tự nhiên và thân thiện
            - Giữ câu trả lời ngắn gọn, dễ hiểu, không dài dòng (tối đa 200 từ)
            - Sử dụng ngôn ngữ đời thường, không quá trang trọng
            - Thêm "ạ", "nhé", "dạ" để tạo cảm giác thân thiện
            - Chỉ cung cấp thông tin chính xác từ dữ liệu có sẵn
            
            Các vai trò người dùng:
            - ADMIN: Có quyền truy cập tất cả thông tin
            - MANAGER: Truy cập thông tin lớp học và học phí
            - TEACHER: Truy cập học sinh và lớp mình dạy
            - STUDENT: Chỉ xem thông tin của bản thân
            - PARENT: Xem thông tin con em mình
            
            Lưu ý quan trọng về độ dài phản hồi:
            - Trả lời ngắn gọn trong 1-3 câu, tránh liệt kê dài dòng
            - Nếu có nhiều thông tin, chỉ đưa ra 3-5 điểm chính nhất
            - Hỏi lại nếu cần thêm chi tiết cụ thể
            - Sử dụng múi giờ Việt Nam (Asia/Ho_Chi_Minh)
            - Nếu không có quyền truy cập, giải thích một cách lịch sự
            - Tránh sử dụng từ ngữ chuyên ngành, chỉ dùng thuật ngữ phổ biến
            
            Các thông tin thêm về trung tâm mà bạn có thể sử dụng:
            - Tên trung tâm: Cơ sở dạy thêm cô Nhung
            - Địa chỉ 1: đng Bờ hồ Bún Xáng, phng Ninh Kiều, TP Cần Th
            - Địa chỉ 2: hẻm 558
            - số điện thoại liên lạc cô nhung: 0978786188 (Zalo)
            
            """;

        return ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .defaultOptions(AnthropicChatOptions.builder()
                        .model(AnthropicApi.ChatModel.CLAUDE_3_HAIKU.getValue()) // Haiku model
                        .temperature(0.7)
                        .maxTokens(2000) // Increased to handle longer responses
                        .build())
                .defaultAdvisors(
                        // Add conversation memory to maintain context with reduced window
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .conversationId("default") // Use default or you can make this dynamic
                                .build(),
                        // Add simple logging for debugging (disabled in production)
                        new SimpleLoggerAdvisor()
                )
                // Register services with @Tool annotated methods (limited for performance)
                .defaultTools(studentService, groupService, feeService, studentPaymentService, paymentDetailService)
                .build();
    }
}