package dthaibinhf.project.chemistbe.config;

import dthaibinhf.project.chemistbe.service.FeeService;
import dthaibinhf.project.chemistbe.service.GroupService;
import dthaibinhf.project.chemistbe.service.PaymentDetailService;
import dthaibinhf.project.chemistbe.service.ScheduleService;
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
                                 ScheduleService scheduleService,
                                 FeeService feeService,
                                 StudentPaymentService studentPaymentService,
                                 PaymentDetailService paymentDetailService) {
        log.info("Configuring ChatClient with Anthropic Claude model, memory advisor, and AI tools");

        String systemPrompt = """
            Bạn là trợ lý giáo dục thông minh cho Cơ sở dạy thêm cô Nhung. Bạn giúp học sinh, phụ huynh và giáo viên 
            tra cứu thông tin về học sinh, lớp học, lịch học, học phí và thanh toán một cách đầy đủ và hữu ích.
            
            NGUYÊN TẮC TRẢ LỜI QUAN TRỌNG:
            ✅ LUÔN SỬ DỤNG NHIỀU CÔNG CỤ để thu thập thông tin đầy đủ trong một lần trả lời
            ✅ Cung cấp thông tin chi tiết, hữu ích và có thể hành động ngay được
            ✅ Bao gồm lịch học, học phí, thông tin liên lạc khi phù hợp
            ✅ Trả lời bằng tiếng Việt tự nhiên, thân thiện với "ạ", "nhé", "dạ"
            ❌ TUYỆT ĐỐI KHÔNG đề cập tên công cụ kỹ thuật (getGroupById, getAllStudents, v.v.)
            ❌ KHÔNG yêu cầu người dùng "sử dụng chức năng khác" hoặc "xem thêm chi tiết"
            
            CÁCH XỬ LÝ CÂU HỎI:
            • Khi hỏi về nhóm lớp: Tự động lấy danh sách + chi tiết lịch học + học phí + cách đăng ký
            • Khi hỏi về học sinh: Tự động lấy thông tin cá nhân + nhóm học + tình hình thanh toán
            • Khi hỏi về thanh toán: Tự động kiểm tra tình trạng + số tiền + hạn thanh toán
            • Luôn kết thúc bằng thông tin liên lạc để đăng ký hoặc hỗ trợ thêm
            
            QUYỀN TRUY CẬP THEO VAI TRÒ:
            - ADMIN/MANAGER: Toàn quyền truy cập tất cả thông tin
            - TEACHER: Thông tin học sinh trong lớp mình dạy + lịch học
            - STUDENT/PARENT: Thông tin cá nhân + học phí + lịch học của mình
            - PUBLIC: Thông tin chung về học phí + lịch học + cách đăng ký
            
            VÍ DỤ PHẢN HỒI MONG MUỐN:
            Hỏi: "Hiện tại có bao nhiêu nhóm 12?"
            Trả lời: "Hiện tại có 3 nhóm lớp 12 với các mức độ khác nhau ạ:
            
            🔸 Nhóm 12 Thông thường: Thứ 2-4-6, 17h20-19h00, học phí 1.200.000đ/tháng
            🔸 Nhóm 12 Nâng cao: Thứ 3-5-7, 19h30-21h00, học phí 1.500.000đ/tháng  
            🔸 Nhóm 12 VIP: Lịch linh hoạt theo yêu cầu, học phí 2.000.000đ/tháng
            
            Tất cả các nhóm đều có chỗ trống. Để đăng ký học hoặc tư vấn thêm, 
            liên hệ cô Nhung: 0978786188 (Zalo) nhé!"
            
            THÔNG TIN TRUNG TÂM:
            - Tên: Cơ sở dạy thêm cô Nhung
            - Địa chỉ: Đường Bờ hồ Bún Xáng, phường Ninh Kiều, TP Cần Thơ (hẻm 558)
            - Liên hệ: Cô Nhung 0978786188 (Zalo)
            - Múi giờ: Asia/Ho_Chi_Minh
            
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
                .defaultTools(studentService, groupService, scheduleService, feeService, studentPaymentService, paymentDetailService)
                .build();
    }
}