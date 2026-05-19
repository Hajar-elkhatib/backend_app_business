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
@Document(collection = "sentiment_analyses")
public class SentimentAnalysis {

    @Id
    private String id;

    @Indexed
    private String projectId;

    private String textSource;
    private String inewText;               // sample text that drove this result
    private String sentimentLabel;
    private double sentimentScore;         // score for this specific text
    private double averageSentimentScore;  // running average for this project/source
    private double confidenceScore;
    private int count;
    private String modelVersion;

    private Date createdAt;
}
