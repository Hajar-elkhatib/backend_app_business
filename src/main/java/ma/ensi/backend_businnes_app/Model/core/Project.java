package ma.ensi.backend_businnes_app.Model.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "projects")
public class Project {

    @Id
    private String id;

    @Indexed
    private String entrepreneurId;     // → Entrepreneur._id

    private String title;
    private String description;

    private String projectStatus;      // "DRAFT" | "SUBMITTED" | "ANALYZING" | "VALIDATED" | "REJECTED"
    private String sector;
    private String country;
    private String countryCode;
    private String region;

    // Founder & team
    private int founderExperienceYears;
    private int fundingRounds;
    private int teamSize;

    // Market & business metrics (inputs to ML model)
    private double marketSizeBillion;
    private double marketGrowthRatePercent;
    private double productTractionUsers;
    private double burnRateMillion;
    private double revenueMillion;

    private String investorType;       // "ANGEL" | "VC" | "BOOTSTRAP" | "GRANT"
    private String competitionLevel;   // "LOW" | "MEDIUM" | "HIGH"
    private double searchTrendScore;
    private boolean userWordBank;

    private String opinions;           // Free-text founder notes

    private Date createdAt;
}