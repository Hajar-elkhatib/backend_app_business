package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class SentimentRequest {
    private String projectId;
    private String text;
    private String textSource;
}