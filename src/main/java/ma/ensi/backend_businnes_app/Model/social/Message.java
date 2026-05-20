package ma.ensi.backend_businnes_app.Model.social;


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
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    @Indexed
    private String chatId;     // → Chat._id

    private String role;       // "USER" | "ASSISTANT" | "SYSTEM"
    private String content;
    private Date timestamp;
    private String senderType; // mirrors role, useful for filtering by sender category
}
