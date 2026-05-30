package ma.ensi.backend_businnes_app.Model.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "market_feedbacks")
public class MarketFeedback {
    @Id
    private String id;

    @Indexed
    private String projectId;

    private String feedbackText;
    private String sentimentLabel;
    private Double sentimentScore;
    private LocalDateTime createdAt;
}
