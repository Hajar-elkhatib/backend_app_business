package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class SendMessageRequest {
    private String conversationId;
    private String senderId;
    private String role;        // "ENTREPRENEUR" | "SPECIALIST"
    private String content;
    private String senderType;
}