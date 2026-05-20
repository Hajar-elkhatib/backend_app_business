package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.DTOS.request.SendMessageRequest;
import ma.ensi.backend_businnes_app.DTOS.response.MessageResponse;
import ma.ensi.backend_businnes_app.Service.ConversationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WSController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationService conversationService;

    public WSController(SimpMessagingTemplate messagingTemplate,
                               ConversationService conversationService) {
        this.messagingTemplate = messagingTemplate;
        this.conversationService = conversationService;
    }

    // Frontend sends to: /app/chat.send
    // Frontend listens on: /topic/conversation/{conversationId}
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request) {

        // 1. Save message to MongoDB
        MessageResponse savedMessage = conversationService.saveMessage(request);

        // 2. Broadcast to all users in this conversation
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + request.getConversationId(),
                savedMessage
        );
    }
}