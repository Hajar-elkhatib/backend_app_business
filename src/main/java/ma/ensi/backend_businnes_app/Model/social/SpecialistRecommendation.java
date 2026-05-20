package ma.ensi.backend_businnes_app.Model.social;


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
@Document(collection = "specialist_recommendations")
public class SpecialistRecommendation {

    @Id
    private String id;

    @Indexed
    private String projectId;        // → Project._id

    @Indexed
    private String specialistId;     // → Specialist._id

    private double recommendedScore; // 0.0 – 1.0
    private int rank;
    private String scoreDetails;     // JSON breakdown of scoring factors
    private String reason;           // human-readable explanation

    private Date createdAt;
}