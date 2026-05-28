package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class CreateChatRequest {
    private String userId;
    private String projectId;
    private String title;
    private String chatLabel;
    private String contextType;
}
