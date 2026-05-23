package ma.ensi.backend_businnes_app.DTOS.response;

import lombok.Data;

@Data
public class MarketAnalysisResponse {
    private String projectId;
    private String sector;
    private String region;
    private double marketSize;
    private double growthRate;
    private String competitionLevel;
    private String priority;
    private double geographicFitScore;
    private double trendScore;
    private double confidenceScore;
    private String modelVersion;
}