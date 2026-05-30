package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.DTOS.request.CreateChatRequest;
import ma.ensi.backend_businnes_app.DTOS.request.ChatbotMessageRequest;
import ma.ensi.backend_businnes_app.DTOS.response.ChatMessageResponse;
import ma.ensi.backend_businnes_app.Model.core.Chat;
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

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestBody CreateChatRequest request) {
        return ResponseEntity.ok(chatbotService.createChat(request));
    }

    @PostMapping("/message")
    public CompletableFuture<ResponseEntity<ChatMessageResponse>> chatMessage(
            @RequestBody ChatbotMessageRequest request) {
        return chatbotService.chatMessage(request).thenApply(ResponseEntity::ok);
    }

    @GetMapping
    public ResponseEntity<List<Chat>> listChats(
            @RequestParam String userId,
            @RequestParam(required = false) String projectId) {
        return ResponseEntity.ok(chatbotService.listChats(userId, projectId));
    }

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<Chat> getChat(@PathVariable String chatId) {
        return ResponseEntity.ok(chatbotService.getChat(chatId));
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(
            @PathVariable String chatId,
            @RequestParam(required = false) String userId) {
        chatbotService.deleteChat(chatId, userId);
        return ResponseEntity.noContent().build();
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
