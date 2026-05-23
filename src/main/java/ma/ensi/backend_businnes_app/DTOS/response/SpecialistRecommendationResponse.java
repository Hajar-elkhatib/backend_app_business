package ma.ensi.backend_businnes_app.DTOS.response;

import lombok.Data;
import java.util.List;

@Data
public class SpecialistRecommendationResponse {
    private String projectId;
    private List<RecommendedSpecialist> recommendations;

    @Data
    public static class RecommendedSpecialist {
        private String specialistId;
        private double recommendedScore;
        private int rank;
        private String scoreDetails;
        private String reason;
    }
}