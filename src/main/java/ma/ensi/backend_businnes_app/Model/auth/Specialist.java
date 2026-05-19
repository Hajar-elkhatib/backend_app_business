package ma.ensi.backend_businnes_app.Model.auth;
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
@Document(collection = "specialists")
public class Specialist {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private String fullName;
    private String profession;
    private String expertiseDomain;
    private List<String> skills;
    private List<String> sectors;
    private int industryExperience;
    private double hourlyRate;
    private double reviewsCount;
    private String location;
    private String languages;
    private double rating;
    private String availabilityStatus;
    private String bio;
    private int completedProjects;

    private Date createdAt;
}