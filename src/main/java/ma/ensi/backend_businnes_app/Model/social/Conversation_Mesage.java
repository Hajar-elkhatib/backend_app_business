package ma.ensi.backend_businnes_app.Model.social;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.Date;

/**
 * ConversationMessage — a single message inside a human-to-human Conversation.
 *
 * Kept separate from chatbot.Message to allow different fields and
 * independent evolution. For example, you might later add:
 *   - read receipts (readAt: Date)
 *   - file attachments (attachmentUrl: String)
 *
 * role: "ENTREPRENEUR" | "SPECIALIST"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversation_messages")
public class Conversation_Mesage {

    @Id
    private String id;

    @Indexed
    private String conversationId; // → Conversation._id

    private String role;           // "ENTREPRENEUR" | "SPECIALIST"
    private String content;
    private Date timestamp;
    private String senderType;
}
