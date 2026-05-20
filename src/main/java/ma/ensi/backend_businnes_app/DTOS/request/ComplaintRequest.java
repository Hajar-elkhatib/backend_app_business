package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class ComplaintRequest {
    private String userId;
    private String subject;
    private String body;
    private String description;
    private String category;  // "BILLING" | "SPECIALIST" | "PLATFORM" | "OTHER"
    private String chatType;
}