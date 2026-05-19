package ma.ensi.backend_businnes_app.Model.social;

import ma.ensi.backend_businnes_app.Model.auth.User;
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
@Document(collection = "complaints")

public class Complaint {

    @Id
    private String id;

    @Indexed
    private String userId;              // → User._id (who filed)

    private String subject;
    private String body;
    private String chatType;
    private String description;
    private String category;            // "BILLING" | "SPECIALIST" | "PLATFORM" | "OTHER"
    private String status;              // "OPEN" | "IN_PROGRESS" | "RESOLVED" | "CLOSED"
    private String aiSuggestedResponse; // AI-generated draft reply for admin

    private Date createdAt;
    private Date resolvedAt;
}
