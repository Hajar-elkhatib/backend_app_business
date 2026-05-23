package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class MarketAnalysisRequest {
    private String projectId;
    private String sector;
    private String region;
    private String countryCode;
    private double marketSize;
    private double growthRate;
    private int productTractionUsers;
    private String competitionLevel;
}