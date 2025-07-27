# AI-Powered Chat Agent Feature

## User Requirements

### Project Context
This is for an undergraduate thesis project demonstrating advanced AI integration with Spring Boot backend systems. The goal is to create an intelligent assistant that can reason about complex queries and take appropriate actions, going beyond simple pattern-matching chatbots.

### Current Tech Stack
- **Backend**: Spring Boot with Java
- **Frontend**: React with TypeScript  
- **Database**: PostgreSQL with JPA/Hibernate
- **Existing Entities**: Student, Attendance, Payment, User, Class, Teacher, Schedule, Group, Score, etc.

### Desired User Experience

**School Administrators** should be able to ask natural language questions like:
- "How many students are in Grade 10?"
- "What's the attendance rate for Class A this month?"
- "Which students have outstanding payments?"
- "Show me today's absent students"
- "Compare attendance between Grade 9 and Grade 10"
- "What's our revenue trend over the past 3 months?"

**Students & Parents** should be able to ask questions like:
- "How many groups are available at Grade 10?"
- "What time slots are available for Grade 11?"
- "How much are the fees for Grade 9?"
- "What's my attendance rate this month?"
- "When is my next exam?"
- "What are my recent grades?"
- "Show me the payment history"
- "What subjects are taught in Class 10A?"

### Key Requirements
1. **NO pattern matching or regex-based parsing** - AI should intelligently analyze questions
2. **Tool-based architecture** - Claude should select which backend functions to call
3. **Multi-step reasoning** - Combine multiple backend functions for complex queries
4. **Context management** - Handle follow-up questions and maintain conversation history
5. **Intelligent reasoning** - AI should explain its process ("I checked attendance records...")
6. **Function calling system** - Backend exposes specific tools that Claude can choose from
7. **Role-based access control** - Different tool access for Admin/Teacher/Student/Parent roles
8. **Data privacy & security** - Students/parents only see their own data
9. **Multi-user support** - Concurrent conversations with proper session isolation

### Expected AI Behavior
1. User asks question in natural language
2. Claude Haiku analyzes what information is needed
3. Claude decides which specific backend tools to call
4. Backend executes chosen functions and returns structured data
5. Claude processes results and responds in natural language
6. AI explains reasoning when needed

## Implementation Plan

### Architecture Overview
The system implements an intelligent AI agent that can analyze natural language queries and select appropriate backend services as tools, rather than using simple pattern matching.

### Core Components

#### 1. AI Agent Service Layer
- **AIAgentService**: Main orchestrator that processes natural language queries
- **ToolRegistryService**: Registers and manages available backend tools  
- **ContextService**: Maintains conversation context and history
- **ResponseFormatterService**: Formats AI responses with data visualization

#### 2. Tool Wrapper System
- **@AITool** annotation to mark service methods as available tools
- **ToolWrapper** classes providing standardized interfaces for existing services
- **Tool metadata** (description, parameters, examples) for AI understanding
- **Input validation** and **error handling** for each tool

#### 3. Claude Integration
- **ClaudeClientService**: Integration with Claude API for function calling
- **Function calling framework**: Let Claude select and execute tools
- **Multistep reasoning**: Support complex queries requiring multiple tool calls
- **Context management**: Maintain conversation history and follow-up capabilities

### Available Tools (From Existing Services)

*Total: 120+ methods across 24 service classes identified through codebase analysis*

#### Student Management Tools [StudentService.java]
- `searchStudents(name, group, school, class, parentPhone)` - Advanced student search with pagination
- `getStudentsByGroup(groupId)` - Get students in a specific group  
- `getStudentDetailHistory(studentId)` - Get student enrollment and group change history
- `getStudentById(studentId)` - Get comprehensive student information
- `getStudentsByGroupId(groupId)` - Get all students in specific group
- `createMultipleStudent(List<StudentDTO>)` - Bulk student creation (Admin only)

#### Attendance & Analytics Tools [AttendanceService.java, StatisticsService.java]
- `searchAttendanceByGroupAndSchedule(groupId, scheduleId)` - Get attendance records with filtering
- `createBulkAttendance(BulkAttendanceDTO)` - Bulk attendance operations (Admin/Teacher only)
- `updateBulkAttendance(BulkAttendanceDTO)` - Bulk attendance updates (Admin/Teacher only)
- `getDashboardStatistics()` - Get comprehensive system metrics (Admin only)

#### Academic Performance Tools [ScoreService.java, ExamService.java]
- `getScoresByStudentId(studentId)` - Get grades/scores for specific student
- `getScoresByExamId(examId)` - Get exam results and statistics
- `getAllExams()` - Get all exams (filtered by access level)
- `getExamById(examId)` - Get specific exam details

#### Schedule & Class Management Tools [ScheduleService.java, GroupService.java, GroupSessionService.java]
- `getAllSchedulesPageable(groupId, startDate, endDate)` - Get schedules with filtering
- `generateWeeklySchedule(groupId, startDate, endDate)` - Auto-generate schedules (Admin/Teacher only)
- `getGroupsByGradeId(gradeLevel)` - Get all groups in specific grade ✅ *Covers "how many groups at grade 10"*
- `getGroupsByAcademicYearId(academicYearId)` - Groups by academic year
- `getAllGroupsWithDetail()` - Detailed group information
- `getAllGroupSessions()` - Get all group sessions ✅ *Covers "what time available"*
- `getGroupSessionById(sessionId)` - Get specific session details

#### Financial & Payment Tools [PaymentDetailService.java, StudentPaymentService.java, PaymentOverdueService.java, FinancialStatisticsService.java]
- `getPaymentDetailsByStudentId(studentId)` - Student payment history (Self-access for students/parents)
- `getStudentPaymentSummaries(studentId)` - Student payment overview
- `getGroupPaymentSummaries(groupId)` - Group payment status (Admin/Teacher only)
- `getOverduePaymentDetails()` - All overdue payments (Admin only)
- `getOverduePaymentsForStudent(studentId)` - Student overdue analysis
- `getFinancialDashboard()` - Comprehensive financial metrics (Admin only)
- `getAllFees()` - Get fee structures ✅ *Covers "how about the fee for each"*
- `getFeeById(feeId)` - Get specific fee details

#### Teacher & Staff Tools [TeacherService.java, SalaryCalculationService.java]
- `searchTeachers(name, phone, email)` - Search teachers with pagination (Admin only)
- `getTeacherSalarySummaries(teacherId)` - Get salary history (Self-access for teachers)
- `getTeacherMonthlySummary(teacherId, month, year)` - Get specific month salary
- `calculateMonthlySalary(teacherId, month, year)` - Calculate monthly teacher salary (Admin only)

### Tool Access Control Matrix

| Tool Category | Admin | Teacher | Student | Parent |
|---------------|--------|---------|---------|--------|
| Student Search (All) | ✅ | ✅ | ❌ | ❌ |
| Student Info (Self) | ✅ | ✅ | ✅ | ✅ |
| Group Information | ✅ | ✅ | ✅ | ✅ |
| Schedule Information | ✅ | ✅ | ✅ | ✅ |
| Fee Information | ✅ | ✅ | ✅ | ✅ |
| Payment Info (Self) | ✅ | ❌ | ✅ | ✅ |
| Payment Info (All) | ✅ | ❌ | ❌ | ❌ |
| Academic Performance (Self) | ✅ | ✅ | ✅ | ✅ |
| Academic Performance (All) | ✅ | ✅ | ❌ | ❌ |
| Financial Analytics | ✅ | ❌ | ❌ | ❌ |
| System Statistics | ✅ | ❌ | ❌ | ❌ |
| Salary Information | ✅ | ✅ (Self) | ❌ | ❌ |

### Implementation Phases

#### Phase 1: Core Infrastructure Setup (2-3 days)

**1.1 Dependencies & Configuration**
```xml
<!-- Option A: Spring AI Anthropic (Recommended) -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-anthropic-spring-boot-starter</artifactId>
    <version>1.0.0-M6</version>
</dependency>

<!-- Option B: Official Anthropic Java SDK -->
<dependency>
    <groupId>com.anthropic</groupId>
    <artifactId>anthropic-java</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- Additional Support -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

**1.2 Core Infrastructure Classes**
- `AIAgentService` - Main orchestration service
- `ClaudeClientService` - Claude API integration  
- `ToolRegistryService` - Tool discovery and management
- `ConversationContextService` - Session and history management
- `AIResponseFormatterService` - Response formatting

**1.3 Configuration Setup**
```properties
# Claude API Configuration  
spring.ai.anthropic.api-key=${ANTHROPIC_API_KEY}
spring.ai.anthropic.chat.options.model=claude-3-5-sonnet-20241022
spring.ai.anthropic.chat.options.temperature=0.2
spring.ai.anthropic.chat.options.max-tokens=2000

# AI Feature Configuration
ai.tool.discovery.enabled=true
ai.conversation.history.max-entries=50
ai.response.streaming.enabled=true
```

#### Phase 2: Tool Framework Development (3-4 days)

**2.1 Tool Annotation System**
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AITool {
    String name();
    String description();
    ToolParameter[] parameters() default {};
    String[] examples() default {};
    String category() default "general";
    boolean requiresAuth() default true;
    String[] allowedRoles() default {"ADMIN"}; // NEW: Role-based access
}
```

**2.2 Tool Registry Architecture**
- **Auto-discovery**: Scan all `@Service` classes for `@AITool` methods
- **JSON Schema generation**: Convert tool definitions to Claude-compatible format
- **Security integration**: Role-based tool access control
- **Performance optimization**: Tool result caching

**2.3 Priority Tool Implementations**

*Tier 1 (Essential - Week 1)*
- Student search and lookup tools
- Group information tools ✅ *For "how many groups at grade 10"*
- Schedule/session tools ✅ *For "what time available"*
- Fee information tools ✅ *For "how about the fee for each"*

*Tier 2 (Important - Week 2)*
- Payment history and status (student/parent access)
- Academic performance tracking (student/parent access)
- Attendance records (student/parent access)

*Tier 3 (Enhanced - Week 3)*
- Advanced analytics for administrators
- Bulk operations and administrative tools

#### Phase 3: AI Agent Core (4-5 days)

**3.1 Natural Language Processing Flow**
```
User Query → Role Detection → Intent Analysis → Tool Selection → Execution → Response Generation
```

**3.2 Multi-Step Reasoning Support**
- **Query decomposition**: Break complex questions into sub-tasks
- **Tool chaining**: Execute multiple tools in sequence
- **Context awareness**: Maintain conversation state
- **Error recovery**: Graceful handling of failed tool calls
- **User context filtering**: Automatic data filtering based on user role and identity

**3.3 Function Calling Implementation**
- **Claude integration**: Use official function calling API with 2025 enhancements
- **Tool definition mapping**: Convert `@AITool` to Claude schema
- **Parameter validation**: Ensure type safety and constraints
- **Result processing**: Format tool outputs for AI consumption

#### Phase 4: API & Security Integration (2-3 days)

**4.1 REST Endpoints**
```java
@RestController
@RequestMapping("/api/v1/ai")
public class AIAgentController {
    
    @PostMapping("/chat")
    public ResponseEntity<AIResponse> chat(@RequestBody ChatRequest request);
    
    @GetMapping("/tools")
    public ResponseEntity<List<ToolDefinition>> getAvailableTools(); // Filtered by user role
    
    @GetMapping("/history/{sessionId}")
    public ResponseEntity<ConversationHistory> getHistory(@PathVariable String sessionId);
    
    @PostMapping("/stream")
    public SseEmitter streamChat(@RequestBody ChatRequest request);
}
```

**4.2 Security Integration**
- **JWT authentication**: Integrate with existing security
- **Role-based access**: Tool permissions by user role (ADMIN/TEACHER/STUDENT/PARENT)
- **Data filtering**: Automatic filtering for student/parent data access
- **Audit logging**: Track all AI interactions
- **Rate limiting**: Prevent API abuse

#### Phase 5: Frontend Integration (3-4 days)

**5.1 React Chat Interface**
- **Role-aware UI**: Different interfaces for different user types
- **Modern chat UI**: Clean, responsive design
- **Real-time messaging**: WebSocket support
- **Message history**: Persistent conversation storage
- **Typing indicators**: Enhanced UX

**5.2 Data Visualization**
- **Chart integration**: Display analytics results (admin view)
- **Table components**: Show structured data
- **Student/Parent dashboard**: Simple, focused information display
- **Export functionality**: PDF/Excel report generation (admin only)
- **Mobile responsiveness**: Cross-device compatibility

### Technical Implementation Details

#### Tool Definition Example
```java
@AITool(
    name = "searchStudents",
    description = "Search for students by various criteria including name, group, school, class, or parent phone",
    parameters = {
        @ToolParameter(name = "studentName", description = "Student's name (partial match allowed)", required = false),
        @ToolParameter(name = "groupName", description = "Group/class name", required = false),
        @ToolParameter(name = "schoolName", description = "School name", required = false),
        @ToolParameter(name = "parentPhone", description = "Parent's phone number", required = false)
    },
    examples = {
        "Find students named 'John'",
        "Search students in 'Class 10A'",
        "Find students whose parents' phone starts with '090'"
    }
)
public ToolExecutionResult searchStudents(String studentName, String groupName, String schoolName, String parentPhone) {
    // Implementation using existing StudentService
}
```

#### AI Agent Service Structure
```java
@Service
public class AIAgentService {
    
    public AIResponse processQuery(String userQuery, String sessionId) {
        // 1. Analyze user intent and extract parameters
        // 2. Select appropriate tools using Claude function calling
        // 3. Execute selected tools in sequence
        // 4. Process results and generate natural language response
        // 5. Update conversation context
    }
    
    public List<ToolDefinition> getAvailableTools() {
        // Return tool registry for Claude's awareness
    }
}
```

### Error Handling & Validation
1. **Tool Execution Errors**
   - Graceful handling of service exceptions
   - Fallback strategies for failed tool calls
   - User-friendly error messages

2. **AI Integration Errors**
   - Claude API timeout handling
   - Invalid function call recovery
   - Context corruption prevention

3. **Security Validation**
   - Parameter sanitization
   - Permission checking before tool execution
   - Audit logging for all AI actions

### Performance Considerations
1. **Caching Strategy**
   - Leverage existing Caffeine cache for tool responses
   - Cache conversation contexts
   - Tool result caching for repeated queries

2. **Response Optimization**
   - Streaming responses for better UX
   - Parallel tool execution where possible
   - Result pagination for large datasets

### Success Metrics
1. **Functionality**: AI correctly selects appropriate tools for >90% of common queries
2. **User Experience**: Average response time <3 seconds for simple queries
3. **Accuracy**: Tool selection accuracy >95% for well-defined question types
4. **Thesis Demonstration**: Successfully shows advanced AI integration beyond simple chatbots

### Example Interaction Flows

#### Administrator Query Examples
```
Admin: "How many students are in Grade 10?"
AI Reasoning: Need student count by grade → Use getGroupsByGradeId(10) + getStudentsByGroup() tools
AI Response: "Grade 10 currently has 45 active students distributed across 3 groups:
- Group 10A: 15 students  
- Group 10B: 16 students
- Group 10C: 14 students"

Admin: "Compare attendance between Grade 9 and Grade 10 this month"
AI Reasoning: 
1. Get groups for Grade 9 → Use getGroupsByGradeId(9)
2. Get groups for Grade 10 → Use getGroupsByGradeId(10)  
3. Calculate attendance for each grade → Use searchAttendanceByGroupAndSchedule()
4. Compare results and provide analysis

AI Response: "This month's attendance comparison:
- Grade 9: 87.3% (245 present / 281 total sessions)
- Grade 10: 92.5% (315 present / 341 total sessions)

Grade 10 has better attendance by 5.2 percentage points."
```

#### Student/Parent Query Examples ✅
```
Student/Parent: "How many groups are available at Grade 10?"
AI Reasoning: User wants enrollment options → Use getGroupsByGradeId(10) tool
AI Response: "Grade 10 has 3 available groups:
- Group 10A: Morning schedule (8:00-11:30 AM)
- Group 10B: Afternoon schedule (1:00-4:30 PM)  
- Group 10C: Evening schedule (6:00-9:30 PM)
Each group has space for new students."

Student/Parent: "What time slots are available for Grade 11?"
AI Reasoning: Need schedule information → Use getAllGroupSessions() filtered by grade 11
AI Response: "Grade 11 has these available time slots:
- Monday/Wednesday/Friday: 8:00-10:00 AM (Group 11A)
- Tuesday/Thursday/Saturday: 2:00-4:00 PM (Group 11B)
- Monday/Wednesday/Friday: 6:00-8:00 PM (Group 11C)"

Student/Parent: "How much are the fees for Grade 9?"
AI Reasoning: Need fee information → Use getAllFees() filtered by grade level
AI Response: "Grade 9 fees for this academic year:
- Monthly tuition: $120
- Registration fee: $50 (one-time)
- Materials fee: $30/semester
- Total first month: $200
- Monthly thereafter: $120"

Student: "What's my attendance rate this month?"
AI Reasoning: Need student's attendance → Use getScoresByStudentId() (auto-filtered to current user)
AI Response: "Your attendance rate for January 2025:
- Present: 18 days
- Absent: 2 days  
- Attendance rate: 90%
- You've attended 18 out of 20 scheduled sessions this month."
```

#### Parent-Specific Query Examples ✅
```
Parent: "Show me my child's payment history"
AI Reasoning: Need payment data → Use getPaymentDetailsByStudentId() (auto-filtered to parent's child)
AI Response: "Payment history for John Smith:
- January 2025: $120 - Paid ✅
- December 2024: $120 - Paid ✅  
- November 2024: $120 - Paid ✅
- Total paid this year: $1,440
- Next payment due: February 15, 2025"

Parent: "When is my child's next exam?"
AI Reasoning: Need exam schedule → Use getAllExams() filtered by student's group
AI Response: "Upcoming exams for John Smith (Group 10A):
- Math midterm: February 10, 2025 (8:00 AM)
- Physics quiz: February 15, 2025 (9:00 AM)
- Chemistry lab test: February 20, 2025 (10:00 AM)"
```

## Student/Parent Access - Detailed Coverage ✅

**Your specific queries are fully covered:**

### "How many groups at grade 10" ✅
- **Tool**: `getGroupsByGradeId(gradeLevel)` from GroupService
- **Access**: Available to ALL user roles (ADMIN/TEACHER/STUDENT/PARENT)
- **Response**: Lists all available groups with basic information

### "What time available" ✅  
- **Tool**: `getAllGroupSessions()` from GroupSessionService
- **Access**: Available to ALL user roles (ADMIN/TEACHER/STUDENT/PARENT)
- **Response**: Shows available time slots filtered by grade/group

### "How about the fee for each" ✅
- **Tool**: `getAllFees()` from FeeService
- **Access**: Available to ALL user roles (ADMIN/TEACHER/STUDENT/PARENT)
- **Response**: Displays fee structure by grade level

### Additional Student/Parent Capabilities
- **Personal data access**: Students/parents can query their own attendance, grades, payment history
- **Schedule information**: View class schedules and upcoming exams
- **Payment status**: Check payment history and outstanding amounts
- **Academic progress**: View grades and performance metrics

### Data Privacy & Security Implementation
- **Automatic filtering**: Students/parents automatically see only their own data
- **Role-based tools**: Different tool sets available based on user role
- **Session isolation**: Each user has private conversation history
- **Audit logging**: All queries tracked for security

## Multi-User Architecture Summary

### User Role Matrix
| User Type | Primary Use Cases | Available Tools | Data Access |
|-----------|------------------|-----------------|-------------|
| **ADMIN** | System analytics, all student/teacher data | Full tool access (120+ methods) | All data |
| **TEACHER** | Class management, student performance | Limited admin tools + class data | Assigned classes only |
| **STUDENT** | Personal info, grades, schedule | Personal data tools | Own data only |
| **PARENT** | Child's progress, payments, schedule | Personal + child data tools | Child's data only |

### Implementation Highlights
- **120+ service methods** mapped as AI tools across 24 service classes
- **Role-based access control** with automatic data filtering
- **Claude 3.5 Sonnet** with function calling for intelligent tool selection
- **Spring AI integration** for seamless Spring Boot compatibility
- **Multi-step reasoning** for complex queries requiring multiple tool calls

### Future Enhancement Opportunities
1. **Advanced Analytics**: Trend analysis and predictive insights (Admin-only)
2. **Automated Reports**: Scheduled report generation via AI
3. **Voice Integration**: Voice-to-text query processing
4. **Mobile Support**: Mobile-optimized chat interface  
5. **Multi-language**: Support for multiple languages in queries
6. **Push Notifications**: AI-driven alerts for important events
7. **Parent Communication**: AI-assisted parent-teacher communication

## Conclusion

This comprehensive plan creates an intelligent agent system that demonstrates advanced AI integration with existing Spring Boot services. The architecture fully supports your requirements for student/parent access while maintaining security and role-based permissions. The system goes far beyond traditional chatbots by using Claude's function calling capabilities to intelligently select and execute backend operations based on natural language queries.

**Perfect for thesis demonstration**: Shows sophisticated AI reasoning, multi-user support, and practical educational management applications.