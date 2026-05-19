package ma.ensi.backend_businnes_app.Model.ai;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ml_models")
public class MLModel {

    @Id
    private String id;

    private String modelName;
    private String modelType;
    private String artifactPath;
    private String trainingDataset;
    private double accuracy;
    private double precisionScore;
    private double recallScore;
    private double f1Score;
    private boolean isFallback;     // use this if primary model is unavailable

    private Date createdAt;
}