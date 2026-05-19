package ma.ensi.backend_businnes_app.Model.social;

import ma.ensi.backend_businnes_app.Model.auth.Entrepreneur;
import  ma.ensi.backend_businnes_app.Model.auth.Specialist;
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
@Document(collection = "conversations")
public class Conversation {

    @Id
    private String id;

    @Indexed
    private String entrepreneurId; // → Entrepreneur._id

    @Indexed
    private String specialistId;   // → Specialist._id

    private String projectId;      // → Project._id (context, nullable)
    private String chatId;         // → Chat._id (if the conversation originated from an AI chat)

    private Date createdAt;
}
