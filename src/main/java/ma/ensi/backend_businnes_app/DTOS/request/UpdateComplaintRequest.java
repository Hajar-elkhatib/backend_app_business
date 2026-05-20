package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class UpdateComplaintRequest {
    private String status;              // "OPEN" | "IN_PROGRESS" | "RESOLVED" | "CLOSED"
    private String aiSuggestedResponse;
}