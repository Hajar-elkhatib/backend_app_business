package ma.ensi.backend_businnes_app.Model.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "business_idea_analyses")
public class BusinessIdeaAnalysis {

    @Id
    private String id;

    @Indexed
    private String projectId;

    private double predictionScore;
    private String predictionLabel;
    private double successProbability;
    private String modelMode;
    private double confidenceScore;
    private double finalScore;
    private double startupSuccessScore;
    private double marketAnalysisScore;
    private double marketOpinionScore;
    private double tractionPerEmployee;
    private double revenuePerUser;
    private double burnToRevenueRatio;

    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> recommendations;
    private List<String> warnings;
    private List<String> generatedNeeds;

    private String interpretation;
    private String interpretationSource;
    private Map<String, Object> shapExplanation;
    private Map<String, Object> rawAnalysis;

    private String modelVersion;
    private Date createdAt;
}
