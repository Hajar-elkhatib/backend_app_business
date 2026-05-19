package ma.ensi.backend_businnes_app.Model.ai;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.List;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ai_responses")
public class AIResponse {

    @Id
    private String id;

    @Indexed
    private String requestId;          // → AIRequest._id

    private String content;
    private String modelId;
    private String responseText;
    private int responseType;
    private String label;
    private String modelName;
    private double confidenceScore;
    private String feedbackMode;

    private List<Double> embeddingVector; // null unless requestType = "EMBEDDING"

    private Date createdAt;
}