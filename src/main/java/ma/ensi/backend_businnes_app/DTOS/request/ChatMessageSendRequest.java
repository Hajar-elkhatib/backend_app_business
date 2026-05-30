package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class ChatMessageSendRequest {
    private String message;
    private Boolean fastMode;
}
