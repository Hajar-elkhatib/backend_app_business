package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class ChatbotMessageRequest {
    private String chatId;
    private String userId;
    private String projectId;
    private String message;
}
