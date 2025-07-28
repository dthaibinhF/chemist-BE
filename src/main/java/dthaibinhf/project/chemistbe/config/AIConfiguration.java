package dthaibinhf.project.chemistbe.config;

import dthaibinhf.project.chemistbe.service.FeeService;
import dthaibinhf.project.chemistbe.service.GroupService;
import dthaibinhf.project.chemistbe.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
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
        log.info("Configuring in-memory chat memory for AI conversations");
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20) // Configure the window size (default is 20)
                .build();
    }

    @Bean
    public ChatClient chatClient(AnthropicChatModel chatModel,
                                 ChatMemory chatMemory,
                                 StudentService studentService,
                                 GroupService groupService,
                                 FeeService feeService) {
        log.info("Configuring ChatClient with Anthropic Claude model, memory advisor, and AI tools");

        String systemPrompt = """
            Bạn là trợ lý giáo dục thông minh cho hệ thống quản lý trường học. Bạn hỗ trợ người dùng truy cập thông tin về 
            học sinh, lớp học, lịch học và học phí.
            
            Cách trả lời của bạn:
            - Luôn trả lời bằng tiếng Việt một cách tự nhiên và thân thiện
            - Giữ câu trả lời ngắn gọn, dễ hiểu, không dài dòng
            - Sử dụng ngôn ngữ đời thường, không quá trang trọng
            - Thêm "ạ", "nhé", "dạ" để tạo cảm giác thân thiện
            - Chỉ cung cấp thông tin chính xác từ dữ liệu có sẵn
            
            Các vai trò người dùng:
            - ADMIN: Có quyền truy cập tất cả thông tin
            - MANAGER: Truy cập thông tin lớp học và học phí
            - TEACHER: Truy cập học sinh và lớp mình dạy
            - STUDENT: Chỉ xem thông tin của bản thân
            - PARENT: Xem thông tin con em mình
            
            Lưu ý quan trọng:
            - Trả lời ngắn gọn, tránh liệt kê dài dòng
            - Nếu có nhiều thông tin, chỉ đưa ra những điểm chính
            - Hỏi lại nếu cần thêm chi tiết cụ thể
            - Sử dụng múi giờ Việt Nam (Asia/Ho_Chi_Minh)
            - Nếu không có quyền truy cập, giải thích một cách lịch sự
            """;

        return ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        // Add conversation memory to maintain context
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .conversationId("default") // Use default or you can make this dynamic
                                .build(),
                        // Add simple logging for debugging
                        new SimpleLoggerAdvisor()
                )
                // Register services with @Tool annotated methods
                .defaultTools(studentService, groupService, feeService)
                .build();
    }
}