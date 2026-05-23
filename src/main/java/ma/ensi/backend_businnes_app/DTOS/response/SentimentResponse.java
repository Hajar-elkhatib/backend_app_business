package ma.ensi.backend_businnes_app.DTOS.response;

import lombok.Data;

@Data
public class SentimentResponse {
    private String projectId;
    private String sentimentLabel;
    private double sentimentScore;
    private double averageSentimentScore;
    private double confidenceScore;
    private String modelVersion;
}