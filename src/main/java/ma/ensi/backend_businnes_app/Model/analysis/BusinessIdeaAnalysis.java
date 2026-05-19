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
@Document(collection = "business_idea_analyses")
public class BusinessIdeaAnalysis {

    @Id
    private String id;

    @Indexed(unique = true)         // One analysis record per project at a time
    private String projectId;       // → Project._id

    private double predictionScore; // raw model output probability
    private String predictionLabel; // "VIABLE" | "RISKY" | "NOT_VIABLE"
    private double confidenceScore;
    private double finalScore;      // weighted composite score
    private double marketAnalysisScore;
    private double tractionPerEmployee;
    private double revenuePerUser;
    private double burnToRevenueRatio;

    private String strengths;         // text summary
    private String weaknesses;        // text summary
    private String warnings;          // critical flags
    private String recommendations;   // high-level action items (detailed ones in Recommendation)

    private String modelVersion;

    private Date createdAt;
}

