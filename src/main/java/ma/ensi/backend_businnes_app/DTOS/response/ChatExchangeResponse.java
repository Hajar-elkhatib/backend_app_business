package ma.ensi.backend_businnes_app.DTOS.response;

import lombok.Data;

import java.util.List;

@Data
public class ChatExchangeResponse {
    private String chatId;
    private ChatMessageResponse userMessage;
    private ChatMessageResponse assistantMessage;
    private List<String> sourcesUsed;
    private String intent;
}
