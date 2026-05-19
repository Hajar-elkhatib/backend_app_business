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
@Document(collection = "competitor_analyses")
public class CompetitorAnalysis {

    @Id
    private String id;

    @Indexed
    private String projectId;            // → Project._id

    @Indexed
    private String marketAnalysisId;     // → MarketAnalysis._id

    private String competitorName;
    private String competitorSector;
    private String strengthLevel;
    private String estimatedMarketShare; // e.g. "12%" or "DOMINANT"
    private String pricePositioning;

    private Date createdAt;
}

