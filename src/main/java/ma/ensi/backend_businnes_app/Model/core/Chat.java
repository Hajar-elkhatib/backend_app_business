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
@Document(collection = "chats")
public class Chat {

    @Id
    private String id;

    @Indexed
    private String userId;      // → User._id

    @Indexed
    private String projectId;   // → Project._id (nullable)

    private String title;
    private String chatLabel;

    private String contextType; // "PROJECT_VALIDATION" | "GENERAL" | "COMPLAINT"

    private Date createdAt;
}
