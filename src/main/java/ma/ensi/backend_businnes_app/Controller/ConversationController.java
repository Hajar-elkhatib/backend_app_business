package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.Model.social.Conversation;
import ma.ensi.backend_businnes_app.DTOS.response.MessageResponse;
import ma.ensi.backend_businnes_app.Service.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = "*")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }


    @PostMapping
    public ResponseEntity<Conversation> startConversation(
            @RequestParam String entrepreneurId,
            @RequestParam String specialistId,
            @RequestParam(required = false) String projectId) {
        return ResponseEntity.ok(
                conversationService.startConversation(entrepreneurId, specialistId, projectId));
    }


    @GetMapping("/entrepreneur/{entrepreneurId}")
    public ResponseEntity<List<Conversation>> getByEntrepreneur(
            @PathVariable String entrepreneurId) {
        return ResponseEntity.ok(
                conversationService.getConversationsByEntrepreneur(entrepreneurId));
    }


    @GetMapping("/specialist/{specialistId}")
    public ResponseEntity<List<Conversation>> getBySpecialist(
            @PathVariable String specialistId) {
        return ResponseEntity.ok(
                conversationService.getConversationsBySpecialist(specialistId));
    }


    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable String conversationId) {
        return ResponseEntity.ok(
                conversationService.getMessages(conversationId));
    }


    @DeleteMapping("/{conversationId}")
    public ResponseEntity<String> deleteConversation(
            @PathVariable String conversationId) {
        conversationService.deleteConversation(conversationId);
        return ResponseEntity.ok("Conversation deleted successfully");
    }
}