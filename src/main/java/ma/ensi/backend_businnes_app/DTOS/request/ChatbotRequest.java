package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;
import java.util.List;

@Data
public class ChatbotRequest {
    private String chatId;
    private String userId;
    private String message;
    private String contextType;
    private String projectId;
    private List<MessageHistory> history;

    @Data
    public static class MessageHistory {
        private String role;
        private String content;
    }
}