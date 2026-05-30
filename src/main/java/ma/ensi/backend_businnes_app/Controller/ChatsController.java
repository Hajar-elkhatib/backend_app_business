package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.DTOS.request.ChatMessageSendRequest;
import ma.ensi.backend_businnes_app.DTOS.request.CreateChatRequest;
import ma.ensi.backend_businnes_app.DTOS.response.ChatExchangeResponse;
import ma.ensi.backend_businnes_app.DTOS.response.ChatMessageResponse;
import ma.ensi.backend_businnes_app.Model.core.Chat;
import ma.ensi.backend_businnes_app.Service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "*")
public class ChatsController {

    private final ChatbotService chatbotService;

    public ChatsController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestBody CreateChatRequest request) {
        return ResponseEntity.ok(chatbotService.createChat(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Chat>> getUserChats(@PathVariable String userId) {
        return ResponseEntity.ok(chatbotService.listChats(userId, null));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<Chat> getChat(@PathVariable String chatId) {
        return ResponseEntity.ok(chatbotService.getChat(chatId));
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(@PathVariable String chatId) {
        return ResponseEntity.ok(chatbotService.getLatestMessages(chatId));
    }

    @PostMapping("/{chatId}/messages")
    public CompletableFuture<ResponseEntity<ChatExchangeResponse>> sendMessage(
            @PathVariable String chatId,
            @RequestBody ChatMessageSendRequest request) {
        return chatbotService.sendChatMessage(chatId, request).thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable String chatId) {
        chatbotService.deleteChat(chatId, null);
        return ResponseEntity.noContent().build();
    }
}
