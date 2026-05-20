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
@Document(collection = "insights")
public class Insight {

    @Id
    private String id;

    @Indexed
    private String projectId;       // → Project._id

    private String title;
    private String description;
    private String insightType;
    private String importanceLevel;

    private Date createdAt;
}
