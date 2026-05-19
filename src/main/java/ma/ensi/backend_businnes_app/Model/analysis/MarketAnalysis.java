package ma.ensi.backend_businnes_app.Model.analysis;

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
@Document(collection = "market_analyses")
public class MarketAnalysis {

    @Id
    private String id;

    @Indexed
    private String projectId;              // → Project._id

    private String sector;
    private String region;
    private String countryCode;
    private double marketSize;
    private double growthRate;
    private String competitionLevel;       // "LOW" | "MEDIUM" | "HIGH"
    private String priority;               // "CRITICAL" | "IMPORTANT" | "MINOR"
    private int productTractionUsers;
    private int competitionCount;
    private double geographicFitScore;
    private double trendScore;
    private double confidenceScore;
    private String keywords;               // comma-separated market keywords used in analysis
    private String dataSource;
    private String modelVersion;

    private Date createdAt;
}

