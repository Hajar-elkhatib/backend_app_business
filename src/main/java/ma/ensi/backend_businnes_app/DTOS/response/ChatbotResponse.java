package ma.ensi.backend_businnes_app.DTOS.response;

import lombok.Data;

@Data
public class ChatbotResponse {
    private String chatId;
    private String response;
    private String modelId;
    private double confidenceScore;
    private String modelVersion;
}