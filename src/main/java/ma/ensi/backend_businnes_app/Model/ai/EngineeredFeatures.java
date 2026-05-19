package ma.ensi.backend_businnes_app.Model.ai;

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
@Document(collection = "engineered_features")
public class EngineeredFeatures {

    @Id
    private String id;

    @Indexed
    private String projectId;              // → Project._id

    private double fundingPerRound;        // fundingRounds / teamSize
    private double experiencePerRound;     // founderExperienceYears / fundingRounds
    private double tractionPerEmployee;    // productTractionUsers / teamSize
    private double burnToRevenueRatio;     // burnRateMillion / revenueMillion
    private double accuracy;              // model accuracy at time of feature engineering
    private double revenuePerUser;         // revenueMillion / productTractionUsers
    private double tractionToMarketRatio;  // productTractionUsers / marketSizeBillion

    private Date createdAt;
}
