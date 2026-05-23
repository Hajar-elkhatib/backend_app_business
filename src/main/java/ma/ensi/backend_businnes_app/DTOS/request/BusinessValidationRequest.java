package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class BusinessValidationRequest {
    private String projectId;
    private String sector;
    private String country;
    private int founderExperienceYears;
    private int fundingRounds;
    private int teamSize;
    private double marketSizeBillion;
    private double marketGrowthRatePercent;
    private double productTractionUsers;
    private double burnRateMillion;
    private double revenueMillion;
    private String investorType;
    private String competitionLevel;
    private double searchTrendScore;
    private boolean userWordBank;
    private String opinions;
}