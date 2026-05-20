package ma.ensi.backend_businnes_app.Service;

import ma.ensi.backend_businnes_app.Model.social.Conversation;
import ma.ensi.backend_businnes_app.Model.social.Conversation_Mesage;
import ma.ensi.backend_businnes_app.Repository.social.ConversationRepository;
import ma.ensi.backend_businnes_app.Repository.social.ConversationMessageRepository;
import ma.ensi.backend_businnes_app.DTOS.request.SendMessageRequest;
import ma.ensi.backend_businnes_app.DTOS.response.MessageResponse;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ConversationMessageRepository conversationMessageRepository;

    public ConversationService(ConversationRepository conversationRepository,
                               ConversationMessageRepository conversationMessageRepository) {
        this.conversationRepository = conversationRepository;
        this.conversationMessageRepository = conversationMessageRepository;
    }

    // Start Conversation
    public Conversation startConversation(String entrepreneurId,
                                          String specialistId,
                                          String projectId) {
        // Check if conversation already exists
        return conversationRepository
                .findByEntrepreneurIdAndSpecialistId(entrepreneurId, specialistId)
                .orElseGet(() -> {
                    Conversation conversation = new Conversation();
                    conversation.setEntrepreneurId(entrepreneurId);
                    conversation.setSpecialistId(specialistId);
                    conversation.setProjectId(projectId);
                    conversation.setCreatedAt(new Date());
                    return conversationRepository.save(conversation);
                });
    }

    // Get All Conversations by Entrepreneur
    public List<Conversation> getConversationsByEntrepreneur(String entrepreneurId) {
        return conversationRepository.findByEntrepreneurId(entrepreneurId);
    }

    //  Get All Conversations by Specialist
    public List<Conversation> getConversationsBySpecialist(String specialistId) {
        return conversationRepository.findBySpecialistId(specialistId);
    }

    //  Get All Messages in Conversation
    public List<MessageResponse> getMessages(String conversationId) {
        return conversationMessageRepository
                .findByConversationIdOrderByTimestampAsc(conversationId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Save Message (called by WebSocket controller)
    public MessageResponse saveMessage(SendMessageRequest request) {
        Conversation_Mesage message = new Conversation_Mesage();
        message.setConversationId(request.getConversationId());
        message.setRole(request.getRole());
        message.setContent(request.getContent());
        message.setSenderType(request.getSenderType());
        message.setTimestamp(new Date());
        Conversation_Mesage saved = conversationMessageRepository.save(message);
        return mapToResponse(saved);
    }

    // Delete Conversation
    public void deleteConversation(String conversationId) {
        conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        conversationMessageRepository.deleteByConversationId(conversationId);
        conversationRepository.deleteById(conversationId);
    }

    // Helper
    private MessageResponse mapToResponse(Conversation_Mesage message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setConversationId(message.getConversationId());
        response.setRole(message.getRole());
        response.setContent(message.getContent());
        response.setSenderType(message.getSenderType());
        response.setTimestamp(message.getTimestamp());
        return response;
    }
}