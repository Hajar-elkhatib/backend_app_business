package ma.ensi.backend_businnes_app.DTOS.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectResponse {
    private String id;
    private String entrepreneurId;
    private String title;
    private String description;
    private String projectStatus;
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

    private Date createdAt;
}