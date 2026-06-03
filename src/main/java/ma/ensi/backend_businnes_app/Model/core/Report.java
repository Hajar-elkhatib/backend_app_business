package ma.ensi.backend_businnes_app.Model.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reports")
public class Report {

    @Id
    private String id;

    @Indexed
    private String projectId;

    private String analysisId;
    private String title;
    private String summary;
    private String reportType;
    private String content;
    private String pdfUrl;
    private String generatedBy;
    private String region;
    private String modelVersion;
    private Date createdAt;
}
