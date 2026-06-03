package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

import java.util.List;

@Data
public class MarketFeedbackRequest {
    private String feedbackText;
    private List<String> feedbacks;
}
