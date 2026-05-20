package ma.ensi.backend_businnes_app.DTOS.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComplaintResponse {
    private String id;
    private String userId;
    private String subject;
    private String body;
    private String description;
    private String category;
    private String status;
    private String chatType;
    private String aiSuggestedResponse;
    private Date createdAt;
    private Date resolvedAt;
}