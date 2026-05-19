package ma.ensi.backend_businnes_app.Model.ai;

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
@Document(collection = "ai_requests")
public class AIRequest {

    @Id
    private String id;

    @Indexed
    private String chatId;        // → Chat._id (null if this is a background analysis request)

    private String prompt;
    private String requestType;
    private int competitionCount;
    private String endpoint;      // which AI endpoint was called (e.g. "gpt-4o", "claude-3")
    private String contextData;   // serialized context passed to the model
    private String payload;       // full JSON payload sent to the API
    private String status;

    private Date createdAt;
}