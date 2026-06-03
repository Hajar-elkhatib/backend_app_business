package ma.ensi.backend_businnes_app.Service;

import ma.ensi.backend_businnes_app.Model.ai.AIRequest;
import ma.ensi.backend_businnes_app.Model.ai.AIResponse;
import ma.ensi.backend_businnes_app.Model.core.Chat;
import ma.ensi.backend_businnes_app.Model.core.Project;
import ma.ensi.backend_businnes_app.Model.social.Message;
import ma.ensi.backend_businnes_app.DTOS.request.ChatMessageSendRequest;
import ma.ensi.backend_businnes_app.DTOS.request.CreateChatRequest;
import ma.ensi.backend_businnes_app.DTOS.request.ChatbotMessageRequest;
import ma.ensi.backend_businnes_app.Repository.chatbot.AIRequestRepository;
import ma.ensi.backend_businnes_app.Repository.chatbot.AIResponseRepository;
import ma.ensi.backend_businnes_app.Repository.core.ChatRepository;
import ma.ensi.backend_businnes_app.Repository.core.ProjectRepository;
import ma.ensi.backend_businnes_app.Repository.social.MessageRepository;
import ma.ensi.backend_businnes_app.DTOS.response.ChatExchangeResponse;
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

    public Chat createChat(CreateChatRequest request) {
        if (request == null || request.getUserId() == null || request.getUserId().isBlank()) {
            throw new RuntimeException("User is required");
        }
        if (request.getProjectId() == null || request.getProjectId().isBlank()) {
            throw new RuntimeException("Project is required");
        }
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Chat chat = new Chat();
        chat.setUserId(request.getUserId());
        chat.setProjectId(request.getProjectId());
        chat.setProjectName(defaultText(project.getTitle(), "Selected project"));
        chat.setTitle(defaultText(request.getTitle(), "Advisor - " + chat.getProjectName()));
        chat.setChatLabel(defaultText(request.getChatLabel(), "AI Assistant"));
        chat.setContextType(defaultText(request.getContextType(), "PROJECT_VALIDATION"));
        chat.setCreatedAt(new Date());
        chat.setUpdatedAt(chat.getCreatedAt());
        return withProjectName(chatRepository.save(chat));
    }

    public List<Chat> listChats(String userId, String projectId) {
        if (userId == null || userId.isBlank()) {
            return List.of();
        }
        List<Chat> chats = chatRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        if (projectId == null || projectId.isBlank()) {
            return chats.stream().map(this::withProjectName).collect(Collectors.toList());
        }
        return chats.stream()
                .filter(chat -> projectId.equals(chat.getProjectId()))
                .map(this::withProjectName)
                .collect(Collectors.toList());
    }

    public Chat getChat(String chatId) {
        return withProjectName(chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found")));
    }

    public void deleteChat(String chatId, String userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        if (userId != null && !userId.isBlank() && !userId.equals(chat.getUserId())) {
            throw new RuntimeException("Chat not found");
        }
        messageRepository.deleteByChatId(chatId);
        chatRepository.deleteById(chatId);
    }

    public CompletableFuture<ChatMessageResponse> chatMessage(ChatbotMessageRequest request) {
        if (request.getChatId() == null || request.getChatId().isBlank()) {
            CreateChatRequest create = new CreateChatRequest();
            create.setUserId(request.getUserId());
            create.setProjectId(request.getProjectId());
            create.setContextType("PROJECT_VALIDATION");
            Chat chat = createChat(create);
            request.setChatId(chat.getId());
        }
        return chat(request.getChatId(), request.getUserId(), request.getMessage());
    }

    public CompletableFuture<ChatExchangeResponse> sendChatMessage(String chatId, ChatMessageSendRequest request) {
        return handleChat(chatId, request == null ? "" : request.getMessage(), request == null || request.getFastMode() == null || request.getFastMode());
    }

    // ✅ Async chat — returns immediately with messageId
    // Frontend polls GET /api/chatbot/{messageId}/status to get response
    @Async
    public CompletableFuture<ChatMessageResponse> chat(String chatId,
                                                       String userId,
                                                       String userMessage) {
        return handleChat(chatId, userMessage, true).thenApply(ChatExchangeResponse::getAssistantMessage);
    }

    @Async
    protected CompletableFuture<ChatExchangeResponse> handleChat(String chatId,
                                                                 String userMessage,
                                                                 boolean fastMode) {
        // 1. Check chat exists
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        if (chat.getProjectId() == null || chat.getProjectId().isBlank()) {
            throw new RuntimeException("This conversation is not linked to a project");
        }
        Project project = projectRepository.findById(chat.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));
        String cleanMessage = defaultText(userMessage, "").trim();
        if (cleanMessage.isBlank()) {
            throw new RuntimeException("Message is required");
        }

        // 2. Save user message immediately
        Message userMsg = new Message();
        userMsg.setChatId(chatId);
        userMsg.setRole("user");
        userMsg.setContent(cleanMessage);
        userMsg.setSenderType("ENTREPRENEUR");
        userMsg.setTimestamp(new Date());
        Message savedUserMsg = messageRepository.save(userMsg);
        updateChatAfterUserMessage(chat, cleanMessage);

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
        request.put("message", cleanMessage);
        request.put("chat_id", chatId);
        request.put("user_id", chat.getUserId());
        request.put("conversation_history", history);
        request.put("project_data", buildProjectContext(project, chat.getContextType()));
        request.put("fast_mode", fastMode);

        // 5. Save AI Request with PENDING status
        AIRequest aiRequest = new AIRequest();
        aiRequest.setChatId(chatId);
        aiRequest.setPrompt(cleanMessage);
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
            String answer = normalizeAnswer(text(response, "answer", ""), cleanMessage, project);

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
            aiMsg.setRole("assistant");
            aiMsg.setContent(answer);
            aiMsg.setSenderType("AI");
            aiMsg.setTimestamp(new Date());
            Message savedAiMsg = messageRepository.save(aiMsg);
            chat.setUpdatedAt(savedAiMsg.getTimestamp());
            chatRepository.save(chat);

            // 10. Return completed response
            ChatExchangeResponse result = new ChatExchangeResponse();
            result.setChatId(chatId);
            result.setUserMessage(toResponse(savedUserMsg));
            result.setAssistantMessage(toResponse(savedAiMsg));
            result.setSourcesUsed(stringList(response.get("sources_used")));
            result.setIntent(text(response, "intent", "business_advisor"));
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

    private Map<String, Object> buildProjectContext(Project project, String contextType) {
        Map<String, Object> projectData = new LinkedHashMap<>();
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
        return projectData;
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

    private Chat withProjectName(Chat chat) {
        if (chat == null || chat.getProjectId() == null || chat.getProjectId().isBlank()) {
            return chat;
        }
        projectRepository.findById(chat.getProjectId())
                .ifPresent(project -> chat.setProjectName(defaultText(project.getTitle(), "Selected project")));
        if (chat.getUpdatedAt() == null) {
            chat.setUpdatedAt(chat.getCreatedAt());
        }
        return chat;
    }

    private void updateChatAfterUserMessage(Chat chat, String userMessage) {
        chat.setUpdatedAt(new Date());
        if (isSignificantTitleMessage(userMessage) && isInitialTitle(chat.getTitle())) {
            chat.setTitle(toConversationTitle(userMessage));
        }
        chatRepository.save(chat);
    }

    private boolean isInitialTitle(String title) {
        if (title == null || title.isBlank()) return true;
        return title.equalsIgnoreCase("New conversation") || title.startsWith("Advisor - ");
    }

    private boolean isSignificantTitleMessage(String message) {
        String lower = message == null ? "" : message.trim().toLowerCase();
        return lower.length() > 8
                && !List.of("hi", "hello", "hey", "bonjour", "salut", "salam").contains(lower);
    }

    private String toConversationTitle(String message) {
        String cleaned = message.replaceAll("\\s+", " ").trim();
        return cleaned.length() <= 45 ? cleaned : cleaned.substring(0, 42).trim() + "...";
    }

    private String normalizeAnswer(String answer, String userMessage, Project project) {
        String value = defaultText(answer, "");
        if (value.isBlank() || value.toLowerCase().contains("no project data received")) {
            if (isGreeting(userMessage)) {
                return "Hello! I can help you analyze " + defaultText(project.getTitle(), "this project")
                        + ", understand its validation score, explore market potential, generate a short business plan, or identify suitable specialists. What would you like to work on?";
            }
            return "I could not prepare a reliable answer for this project yet. Please try again with a more specific question about the score, market, risks, business plan, or next steps.";
        }
        return value;
    }

    private boolean isGreeting(String message) {
        String lower = message == null ? "" : message.trim().toLowerCase();
        return List.of("hi", "hello", "hey", "bonjour", "salut", "salam").contains(lower);
    }

    private ChatMessageResponse toResponse(Message message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setChatId(message.getChatId());
        response.setRole(message.getRole());
        response.setContent(message.getContent());
        response.setSenderType(message.getSenderType());
        response.setTimestamp(message.getTimestamp());
        return response;
    }

    private List<String> stringList(Object value) {
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).collect(Collectors.toList());
        }
        if (value instanceof String text && !text.isBlank()) {
            return List.of(text);
        }
        return List.of();
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
