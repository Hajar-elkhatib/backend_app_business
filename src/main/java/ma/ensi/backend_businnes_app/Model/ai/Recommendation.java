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
@Document(collection = "recommendations")
public class Recommendation {

    @Id
    private String id;

    @Indexed
    private String projectId;           // → Project._id

    @Indexed
    private String analysisId;          // → BusinessIdeaAnalysis._id

    private String recommendationType;
    private String description;
    private double confidenceScore;

    private Date createdAt;
}