package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class StartupSuccessRequest {
    private String projectId;
    private int founderExperienceYears;
    private int fundingRounds;
    private int teamSize;
    private double burnRateMillion;
    private double revenueMillion;
    private double marketSizeBillion;
    private String investorType;
}