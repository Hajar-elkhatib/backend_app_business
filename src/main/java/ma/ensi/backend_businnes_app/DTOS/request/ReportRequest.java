package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class ReportRequest {
    private String projectId;
    private String title;
    private String summary;
    private String reportType;   // "AI_GENERATED" | "SPECIALIST" | "COMBINED"
    private String region;
    private String modelVersion;
}