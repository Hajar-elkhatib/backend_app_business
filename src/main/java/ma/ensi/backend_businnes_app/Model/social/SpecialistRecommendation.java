package ma.ensi.backend_businnes_app.Model.social;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "specialist_recommendations")
public class SpecialistRecommendation {

    @Id
    private String id;

    @Indexed
    private String projectId;

    @Indexed
    private String specialistId;

    private String specialistName;
    private String expertiseDomain;
    private double recommendedScore;
    private int rank;
    private String scoreDetails;
    private String reason;
    private String generatedNeeds;
    private Date createdAt;
}
