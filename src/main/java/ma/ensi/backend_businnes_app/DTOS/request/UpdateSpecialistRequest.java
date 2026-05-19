package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;
import java.util.List;

@Data
public class UpdateSpecialistRequest {
    private String fullName;
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
    private String availabilityStatus;
}
