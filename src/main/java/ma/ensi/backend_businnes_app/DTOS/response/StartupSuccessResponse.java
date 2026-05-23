package ma.ensi.backend_businnes_app.DTOS.response;

import lombok.Data;

@Data
public class StartupSuccessResponse {
    private String projectId;
    private double successProbability;
    private String predictionLabel;
    private double confidenceScore;
    private String modelVersion;
}