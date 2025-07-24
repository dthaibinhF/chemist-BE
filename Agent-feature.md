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
Users (school administrators) should be able to ask natural language questions like:
- "How many students are in Grade 10?"
- "What's the attendance rate for Class A this month?"
- "Which students have outstanding payments?"
- "Show me today's absent students"
- "Compare attendance between Grade 9 and Grade 10"
- "What's our revenue trend over the past 3 months?"

### Key Requirements
1. **NO pattern matching or regex-based parsing** - AI should intelligently analyze questions
2. **Tool-based architecture** - Claude should select which backend functions to call
3. **Multi-step reasoning** - Combine multiple backend functions for complex queries
4. **Context management** - Handle follow-up questions and maintain conversation history
5. **Intelligent reasoning** - AI should explain its process ("I checked attendance records...")
6. **Function calling system** - Backend exposes specific tools that Claude can choose from

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
- **Multi-step reasoning**: Support complex queries requiring multiple tool calls
- **Context management**: Maintain conversation history and follow-up capabilities

### Available Tools (From Existing Services)

#### Student Management Tools
- `searchStudents(name, group, school, class, parentPhone)` - Advanced student search with pagination
- `getStudentsByGroup(groupId)` - Get students in specific group  
- `getStudentHistory(studentId)` - Get student enrollment and group change history
- `getStudentDetails(studentId)` - Get comprehensive student information

#### Attendance & Analytics Tools  
- `calculateAttendanceRate(groupId, startDate, endDate)` - Calculate attendance rates
- `getAttendanceData(groupId, scheduleId, dateRange)` - Get attendance records with filtering
- `getTodaysAbsentStudents()` - Get students absent today
- `compareGroupAttendance(groupId1, groupId2, period)` - Compare attendance between groups
- `getDashboardStatistics()` - Get comprehensive system metrics

#### Academic Performance Tools
- `getStudentGrades(studentId, examId)` - Get grades/scores for students or exams
- `compareClassPerformance(class1, class2, metric)` - Compare performance between classes
- `getExamResults(examId)` - Get exam results and statistics
- `getGradeAnalytics(gradeLevel, period)` - Analyze performance by grade level

#### Schedule & Class Management Tools
- `getScheduleData(groupId, dateRange)` - Get schedules with filtering
- `getClassInformation(classId)` - Get class and group details
- `getTeacherSchedule(teacherId, dateRange)` - Get teacher assignments
- `getGroupsByGrade(gradeLevel)` - Get all groups in specific grade
- `getRoomUtilization(roomId, period)` - Analyze room usage

#### Financial & Payment Tools
- `getOutstandingPayments()` - Get students with unpaid fees
- `getPaymentStatus(studentId)` - Get specific student payment status
- `getRevenueAnalytics(startDate, endDate)` - Calculate revenue trends
- `getFeeInformation(feeId)` - Get fee structures and details
- `getPaymentHistory(studentId, period)` - Get payment history

#### Teacher & Staff Tools
- `searchTeachers(name, phone, email)` - Search teachers with pagination
- `getTeacherWorkload(teacherId)` - Analyze teacher schedule load
- `getTeacherPerformance(teacherId, metric)` - Get teacher performance metrics

### Implementation Phases

#### Phase 1: Core Infrastructure Setup
1. **Add Dependencies**
   - Claude API client library
   - Function calling framework dependencies
   - Chat-related Spring Boot starters

2. **Create Base Services**
   - `AIAgentService` - Core AI orchestration
   - `ClaudeClientService` - Claude API integration
   - `ToolRegistryService` - Tool management

3. **Design Tool Interface**
   - `@AITool` annotation
   - `ToolMetadata` class for tool descriptions
   - `ToolExecutionResult` wrapper class

#### Phase 2: Tool Wrapper Development  
1. **Wrap Existing Services**
   - Create tool wrappers for each service category
   - Add tool metadata and parameter descriptions
   - Implement input validation and error handling

2. **Tool Registration**
   - Auto-discover tools using Spring's component scanning
   - Build tool registry with descriptions and examples
   - Create tool execution framework

#### Phase 3: AI Agent Implementation
1. **Core AI Logic**
   - Implement natural language query processing
   - Add tool selection and execution logic
   - Handle multi-step reasoning patterns

2. **Context Management**
   - Conversation history storage
   - Follow-up question handling
   - Session management

#### Phase 4: API Integration
1. **REST Endpoints**
   - `POST /api/v1/ai/chat` - Main chat endpoint
   - `GET /api/v1/ai/tools` - List available tools
   - `GET /api/v1/ai/history/{sessionId}` - Get conversation history

2. **Security Integration**
   - Integrate with existing JWT authentication
   - Role-based tool access control
   - Audit logging for AI actions

#### Phase 5: Frontend Integration
1. **React Chat Interface**
   - Chat UI components
   - Message history display
   - Real-time response streaming

2. **Data Visualization**
   - Charts and graphs for analytics responses
   - Tables for student/teacher data
   - Export capabilities for AI-generated reports

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

#### Simple Query Example
```
User: "How many students are in Grade 10?"
AI Reasoning: Need student count by grade → Use getStudentsByGrade(10) tool
AI Response: "Grade 10 currently has 45 active students distributed across 3 classes (10A: 15 students, 10B: 16 students, 10C: 14 students)."
```

#### Complex Multi-Step Query Example  
```
User: "Compare attendance between Grade 9 and Grade 10 this month"
AI Reasoning: 
1. Need attendance data for Grade 9 → Use calculateAttendanceRate(grade=9, thisMonth)
2. Need attendance data for Grade 10 → Use calculateAttendanceRate(grade=10, thisMonth)  
3. Compare results and provide analysis

AI Response: "This month's attendance comparison:
- Grade 9: 87.3% (245 present / 281 total sessions)
- Grade 10: 92.5% (315 present / 341 total sessions)

Grade 10 has better attendance by 5.2 percentage points. This could be due to Grade 10 students being more focused on their final year studies."
```

### Future Enhancement Opportunities
1. **Advanced Analytics**: Trend analysis and predictive insights
2. **Automated Reports**: Scheduled report generation via AI
3. **Voice Integration**: Voice-to-text query processing
4. **Mobile Support**: Mobile-optimized chat interface
5. **Multi-language**: Support for multiple languages in queries

This comprehensive plan creates an intelligent agent system that demonstrates advanced AI integration with existing Spring Boot services, perfect for a thesis project showcasing sophisticated AI reasoning capabilities beyond traditional chatbot approaches.