package ma.ensi.backend_businnes_app.Service;

import ma.ensi.backend_businnes_app.Model.ai.AIRequest;
import ma.ensi.backend_businnes_app.Model.ai.AIResponse;
import ma.ensi.backend_businnes_app.Model.core.Chat;
import ma.ensi.backend_businnes_app.Model.social.Message;
import ma.ensi.backend_businnes_app.Repository.chatbot.AIRequestRepository;
import ma.ensi.backend_businnes_app.Repository.chatbot.AIResponseRepository;
import ma.ensi.backend_businnes_app.Repository.core.ChatRepository;
import ma.ensi.backend_businnes_app.Repository.social.MessageRepository;
import ma.ensi.backend_businnes_app.DTOS.request.ChatbotRequest;
import ma.ensi.backend_businnes_app.DTOS.response.ChatbotResponse;
import ma.ensi.backend_businnes_app.DTOS.response.ChatMessageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    private final RestTemplate restTemplate;
    private final String fastApiBaseUrl;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final AIRequestRepository aiRequestRepository;
    private final AIResponseRepository aiResponseRepository;

    public ChatbotService(
            RestTemplate restTemplate,
            @Value("${fastapi.base-url}") String fastApiBaseUrl,
            ChatRepository chatRepository,
            MessageRepository messageRepository,
            AIRequestRepository aiRequestRepository,
            AIResponseRepository aiResponseRepository) {
        this.restTemplate = restTemplate;
        this.fastApiBaseUrl = fastApiBaseUrl;
        this.chatRepository = chatRepository;
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
        List<ChatbotRequest.MessageHistory> history = messageRepository
                .findByChatIdOrderByTimestampAsc(chatId)
                .stream()
                .map(m -> {
                    ChatbotRequest.MessageHistory h =
                            new ChatbotRequest.MessageHistory();
                    h.setRole(m.getRole());
                    h.setContent(m.getContent());
                    return h;
                }).collect(Collectors.toList());

        // 4. Build request
        ChatbotRequest request = new ChatbotRequest();
        request.setChatId(chatId);
        request.setUserId(userId);
        request.setMessage(userMessage);
        request.setContextType(chat.getContextType());
        request.setProjectId(chat.getProjectId());
        request.setHistory(history);

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
            ChatbotResponse response = restTemplate.postForObject(
                    fastApiBaseUrl + "/api/v1/chatbot/message",
                    request,
                    ChatbotResponse.class
            );

            // 7. Save AI Response
            AIResponse aiResponse = new AIResponse();
            aiResponse.setRequestId(savedRequest.getId());
            aiResponse.setContent(response.getResponse());
            aiResponse.setModelId(response.getModelId());
            aiResponse.setModelName("chatbot");
            aiResponse.setConfidenceScore(response.getConfidenceScore());
            aiResponse.setCreatedAt(new Date());
            aiResponseRepository.save(aiResponse);

            // 8. Update request status to COMPLETED
            savedRequest.setStatus("COMPLETED");
            aiRequestRepository.save(savedRequest);

            // 9. Save AI message
            Message aiMsg = new Message();
            aiMsg.setChatId(chatId);
            aiMsg.setRole("ASSISTANT");
            aiMsg.setContent(response.getResponse());
            aiMsg.setSenderType("AI");
            aiMsg.setTimestamp(new Date());
            Message savedAiMsg = messageRepository.save(aiMsg);

            // 10. Return completed response
            ChatMessageResponse result = new ChatMessageResponse();
            result.setId(savedAiMsg.getId());
            result.setChatId(chatId);
            result.setRole("ASSISTANT");
            result.setContent(response.getResponse());
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
}