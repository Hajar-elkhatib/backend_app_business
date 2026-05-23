package ma.ensi.backend_businnes_app.DTOS.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {
    private String id;
    private String projectId;
    private String title;
    private String summary;
    private String reportType;
    private String pdfUrl;
    private String region;
    private String modelVersion;
    private Date createdAt;
}