package ma.ensi.backend_businnes_app.DTOS.response;

import lombok.Data;

@Data
public class BusinessValidationResponse {
    private String projectId;
    private double predictionScore;
    private String predictionLabel;
    private double confidenceScore;
    private double finalScore;
    private double marketAnalysisScore;
    private double tractionPerEmployee;
    private double revenuePerUser;
    private double burnToRevenueRatio;
    private String strengths;
    private String weaknesses;
    private String warnings;
    private String recommendations;
    private String modelVersion;
}