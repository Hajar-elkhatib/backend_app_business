package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.DTOS.response.ChatMessageResponse;
import ma.ensi.backend_businnes_app.Service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    // ✅ Send message — returns immediately
    // Frontend shows "thinking..." then polls for response
    @PostMapping("/{chatId}/message")
    public CompletableFuture<ResponseEntity<ChatMessageResponse>> chat(
            @PathVariable String chatId,
            @RequestParam String userId,
            @RequestParam String message) {
        return chatbotService.chat(chatId, userId, message)
                .thenApply(ResponseEntity::ok);
    }

    // ✅ Poll status
    @GetMapping("/status/{requestId}")
    public ResponseEntity<String> checkStatus(
            @PathVariable String requestId) {
        return ResponseEntity.ok(chatbotService.checkStatus(requestId));
    }

    // ✅ Get all messages — frontend polls this to show new AI response
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable String chatId) {
        return ResponseEntity.ok(chatbotService.getLatestMessages(chatId));
    }
}