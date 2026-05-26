package ma.ensi.backend_businnes_app.Service;

import ma.ensi.backend_businnes_app.Model.ai.AIRequest;
import ma.ensi.backend_businnes_app.Model.ai.AIResponse;
import ma.ensi.backend_businnes_app.Model.core.Chat;
import ma.ensi.backend_businnes_app.Model.social.Message;
import ma.ensi.backend_businnes_app.Repository.chatbot.AIRequestRepository;
import ma.ensi.backend_businnes_app.Repository.chatbot.AIResponseRepository;
import ma.ensi.backend_businnes_app.Repository.core.ChatRepository;
import ma.ensi.backend_businnes_app.Repository.core.ProjectRepository;
import ma.ensi.backend_businnes_app.Repository.social.MessageRepository;
import ma.ensi.backend_businnes_app.DTOS.response.ChatMessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    private final RestTemplate restTemplate;
    private final String chatbotApiBaseUrl;
    private final ChatRepository chatRepository;
    private final ProjectRepository projectRepository;
    private final MessageRepository messageRepository;
    private final AIRequestRepository aiRequestRepository;
    private final AIResponseRepository aiResponseRepository;

    public ChatbotService(
            RestTemplate restTemplate,
            @Value("${fastapi.chatbot-base-url:${fastapi.base-url}}") String chatbotApiBaseUrl,
            ChatRepository chatRepository,
            ProjectRepository projectRepository,
            MessageRepository messageRepository,
            AIRequestRepository aiRequestRepository,
            AIResponseRepository aiResponseRepository) {
        this.restTemplate = restTemplate;
        this.chatbotApiBaseUrl = trimTrailingSlash(chatbotApiBaseUrl);
        this.chatRepository = chatRepository;
        this.projectRepository = projectRepository;
        this.messageRepository = messageRepository;
        this.aiRequestRepository = aiRequestRepository;
        this.aiResponseRepository = aiResponseRepository;
    }

    // ✅ Async chat — returns immediately with messageId
    // Frontend polls GET /api/chatbot/{messageId}/status to get response
    @Async
    public CompletableFuture<ChatMessageResponse> chat(String chatId,
                                                       String userId,
                                                       String userMessage) {
        // 1. Check chat exists
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        // 2. Save user message immediately
        Message userMsg = new Message();
        userMsg.setChatId(chatId);
        userMsg.setRole("USER");
        userMsg.setContent(userMessage);
        userMsg.setSenderType("ENTREPRENEUR");
        userMsg.setTimestamp(new Date());
        messageRepository.save(userMsg);

        // 3. Load history
        List<Map<String, Object>> history = messageRepository
                .findByChatIdOrderByTimestampAsc(chatId)
                .stream()
                .map(m -> {
                    Map<String, Object> h = new LinkedHashMap<>();
                    h.put("role", m.getRole());
                    h.put("content", m.getContent());
                    return h;
                }).collect(Collectors.toList());

        // 4. Build request
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("message", userMessage);
        request.put("chat_id", chatId);
        request.put("user_id", userId);
        request.put("conversation_history", history);
        request.put("project_data", buildProjectContext(chat.getProjectId(), chat.getContextType()));

        // 5. Save AI Request with PENDING status
        AIRequest aiRequest = new AIRequest();
        aiRequest.setChatId(chatId);
        aiRequest.setPrompt(userMessage);
        aiRequest.setRequestType("CHATBOT");
        aiRequest.setEndpoint("/api/v1/chatbot/message");
        aiRequest.setStatus("PENDING");
        aiRequest.setCreatedAt(new Date());
        AIRequest savedRequest = aiRequestRepository.save(aiRequest);

        try {
            // 6. Call FastAPI (this takes time — runs in background)
            Map<String, Object> response = restTemplate.postForObject(
                    chatbotApiBaseUrl + "/api/v1/chatbot/message",
                    request,
                    Map.class
            );
            if (response == null) {
                throw new RuntimeException("Empty chatbot response");
            }
            String answer = text(response, "answer", "");

            // 7. Save AI Response
            AIResponse aiResponse = new AIResponse();
            aiResponse.setRequestId(savedRequest.getId());
            aiResponse.setContent(answer);
            aiResponse.setModelId(text(response, "intent", "chatbot"));
            aiResponse.setModelName("chatbot");
            aiResponse.setConfidenceScore(100.0);
            aiResponse.setCreatedAt(new Date());
            aiResponseRepository.save(aiResponse);

            // 8. Update request status to COMPLETED
            savedRequest.setStatus("COMPLETED");
            aiRequestRepository.save(savedRequest);

            // 9. Save AI message
            Message aiMsg = new Message();
            aiMsg.setChatId(chatId);
            aiMsg.setRole("ASSISTANT");
            aiMsg.setContent(answer);
            aiMsg.setSenderType("AI");
            aiMsg.setTimestamp(new Date());
            Message savedAiMsg = messageRepository.save(aiMsg);

            // 10. Return completed response
            ChatMessageResponse result = new ChatMessageResponse();
            result.setId(savedAiMsg.getId());
            result.setChatId(chatId);
            result.setRole("ASSISTANT");
            result.setContent(answer);
            result.setSenderType("AI");
            result.setTimestamp(savedAiMsg.getTimestamp());

            return CompletableFuture.completedFuture(result);

        } catch (Exception e) {
            // If FastAPI fails update status to FAILED
            savedRequest.setStatus("FAILED");
            aiRequestRepository.save(savedRequest);
            throw new RuntimeException("Chatbot error: " + e.getMessage());
        }
    }

    // ✅ Check status of AI request
    public String checkStatus(String requestId) {
        AIRequest aiRequest = aiRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        return aiRequest.getStatus();
    }

    // ✅ Get latest messages of chat
    public List<ChatMessageResponse> getLatestMessages(String chatId) {
        return messageRepository
                .findByChatIdOrderByTimestampAsc(chatId)
                .stream()
                .map(m -> {
                    ChatMessageResponse r = new ChatMessageResponse();
                    r.setId(m.getId());
                    r.setChatId(m.getChatId());
                    r.setRole(m.getRole());
                    r.setContent(m.getContent());
                    r.setSenderType(m.getSenderType());
                    r.setTimestamp(m.getTimestamp());
                    return r;
                }).collect(Collectors.toList());
    }

    private Map<String, Object> buildProjectContext(String projectId, String contextType) {
        Map<String, Object> projectData = new LinkedHashMap<>();
        if (projectId != null && !projectId.isBlank()) {
            projectRepository.findById(projectId).ifPresent(project -> {
                if (contextType != null && !contextType.isBlank()) {
                    projectData.put("context_type", contextType);
                }
                projectData.put("project_id", project.getId());
                projectData.put("project_name", defaultText(project.getTitle(), "Untitled project"));
                projectData.put("project_description", defaultText(project.getDescription(), ""));
                projectData.put("title", defaultText(project.getTitle(), "Untitled project"));
                projectData.put("description", defaultText(project.getDescription(), ""));
                projectData.put("sector", defaultText(project.getSector(), "Other"));
                projectData.put("keyword", project.getSector());
                projectData.put("country", defaultText(project.getCountry(), "Morocco"));
                projectData.put("country_code", project.getCountryCode());
                projectData.put("region", project.getRegion());
                projectData.put("founder_experience_years", project.getFounderExperienceYears());
                projectData.put("funding_rounds", Math.max(project.getFundingRounds(), 1));
                projectData.put("team_size", Math.max(project.getTeamSize(), 1));
                projectData.put("market_size_billion", Math.max(project.getMarketSizeBillion(), 0.0));
                projectData.put("market_growth_rate_percent", project.getMarketGrowthRatePercent());
                projectData.put("product_traction_users", (int) Math.max(project.getProductTractionUsers(), 0.0));
                projectData.put("burn_rate_million", Math.max(project.getBurnRateMillion(), 0.0));
                projectData.put("revenue_million", Math.max(project.getRevenueMillion(), 0.000001));
                projectData.put("investor_type", defaultText(project.getInvestorType(), "none"));
                projectData.put("founder_background", "first_time");
                projectData.put("competition_level", project.getCompetitionLevel());
                projectData.put("search_trend_score", project.getSearchTrendScore());
                projectData.put("use_world_bank", project.isUserWordBank());
                projectData.put("project_stage", project.getProjectStatus());
                projectData.put("location", project.getRegion());
                projectData.put("top_k", 5);
                projectData.put("opinions", splitOpinions(project.getOpinions()));
                projectData.put("needs", splitOpinions(project.getOpinions()));
            });
        }
        return projectData.isEmpty() ? null : projectData;
    }

    private String defaultText(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private List<String> splitOpinions(String opinions) {
        if (opinions == null || opinions.isBlank()) {
            return List.of();
        }
        return Arrays.stream(opinions.split("\\r?\\n|;"))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .collect(Collectors.toList());
    }

    private String text(Map<String, Object> source, String key, String defaultValue) {
        Object value = source.get(key);
        return value == null ? defaultValue : String.valueOf(value);
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
