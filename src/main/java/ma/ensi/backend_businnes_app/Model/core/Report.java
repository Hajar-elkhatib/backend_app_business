package ma.ensi.backend_businnes_app.Model.core;

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
@Document(collection = "reports")
public class Report {

    @Id
    private String id;

    @Indexed
    private String projectId;     // → Project._id

    private String title;
    private String summary;
    private String reportType;    // "AI_GENERATED" | "SPECIALIST" | "COMBINED"
    private String pdfUrl;        // e.g. "https://storage/reports/report-xyz.pdf"
    private String region;
    private String modelVersion;  // which ML model version produced this report

    private Date createdAt;
}

