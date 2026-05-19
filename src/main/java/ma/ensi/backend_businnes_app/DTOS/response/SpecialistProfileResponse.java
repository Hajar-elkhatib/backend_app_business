package ma.ensi.backend_businnes_app.DTOS.response;



import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialistProfileResponse {
    private String id;
    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String profession;
    private String expertiseDomain;
    private List<String> skills;
    private List<String> sectors;
    private String location;
    private String languages;
    private double hourlyRate;
    private int industryExperience;
    private String bio;
    private double rating;
    private String availabilityStatus;
    private int completedProjects;
}
