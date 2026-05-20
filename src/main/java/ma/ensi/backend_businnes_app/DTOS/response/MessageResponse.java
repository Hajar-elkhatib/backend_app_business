package ma.ensi.backend_businnes_app.DTOS.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String id;
    private String conversationId;
    private String role;
    private String content;
    private String senderType;
    private Date timestamp;
}