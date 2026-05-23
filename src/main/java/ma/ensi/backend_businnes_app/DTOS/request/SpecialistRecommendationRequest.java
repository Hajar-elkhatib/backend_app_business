package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;
import java.util.List;

@Data
public class SpecialistRecommendationRequest {
    private String projectId;
    private String sector;
    private String requiredExpertise;
    private List<String> requiredSkills;
    private List<SpecialistData> specialists;

    @Data
    public static class SpecialistData {
        private String specialistId;
        private String profession;
        private String expertiseDomain;
        private List<String> skills;
        private List<String> sectors;
        private double rating;
        private int industryExperience;
        private double hourlyRate;
        private String availabilityStatus;
    }
}