package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class UpdateProjectRequest {
    private String title;
    private String description;
    private String sector;
    private String country;
    private String countryCode;
    private String region;

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