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
    public String processQuery(String userQuery, String conversationId) {
        try {
            log.info("Processing AI query for conversation {}: {}", conversationId, userQuery);
            
            return chatClient.prompt()
                    .user(userQuery)
                    .advisors(advisorSpec -> advisorSpec.param("conversationId", conversationId))
                    .call()
                    .content();
                    
        } catch (Exception e) {
            log.error("Error processing AI query for conversation {}: {}", conversationId, e.getMessage(), e);
            return "I apologize, but I encountered an error while processing your request. Please try again.";
        }
    }

    /**
     * Process a user query without conversation context (stateless)
     * 
     * @param userQuery The user's natural language query
     * @return AI response as string
     */
    public String processQuery(String userQuery) {
        try {
            log.info("Processing stateless AI query: {}", userQuery);
            
            return chatClient.prompt()
                    .user(userQuery)
                    .call()
                    .content();
                    
        } catch (Exception e) {
            log.error("Error processing stateless AI query: {}", e.getMessage(), e);
            return "I apologize, but I encountered an error while processing your request. Please try again.";
        }
    }

    /**
     * Process a user query with streaming response for real-time updates
     * 
     * @param userQuery The user's natural language query
     * @param conversationId Unique conversation ID for memory context
     * @return Flux of response chunks for streaming
     */
    public Flux<String> streamQuery(String userQuery, String conversationId) {
        try {
            log.info("Processing streaming AI query for conversation {}: {}", conversationId, userQuery);
            
            return chatClient.prompt()
                    .user(userQuery)
                    .advisors(advisorSpec -> advisorSpec.param("conversationId", conversationId))
                    .stream()
                    .content();
                    
        } catch (Exception e) {
            log.error("Error processing streaming AI query for conversation {}: {}", conversationId, e.getMessage(), e);
            return Flux.just("I apologize, but I encountered an error while processing your request. Please try again.");
        }
    }

    /**
     * Process a user query with streaming response (stateless)
     * 
     * @param userQuery The user's natural language query
     * @return Flux of response chunks for streaming
     */
    public Flux<String> streamQuery(String userQuery) {
        try {
            log.info("Processing stateless streaming AI query: {}", userQuery);
            
            return chatClient.prompt()
                    .user(userQuery)
                    .stream()
                    .content();
                    
        } catch (Exception e) {
            log.error("Error processing stateless streaming AI query: {}", e.getMessage(), e);
            return Flux.just("I apologize, but I encountered an error while processing your request. Please try again.");
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
    public String processQueryWithContext(String userQuery, String systemMessage, String conversationId) {
        try {
            log.info("Processing AI query with custom system message for conversation {}", conversationId);
            
            return chatClient.prompt()
                    .system(systemMessage)
                    .user(userQuery)
                    .advisors(advisorSpec -> advisorSpec.param("conversationId", conversationId))
                    .call()
                    .content();
                    
        } catch (Exception e) {
            log.error("Error processing AI query with context for conversation {}: {}", conversationId, e.getMessage(), e);
            return "I apologize, but I encountered an error while processing your request. Please try again.";
        }
    }
}