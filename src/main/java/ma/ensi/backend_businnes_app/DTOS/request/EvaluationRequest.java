package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;
import java.time.LocalTime;
import java.util.Date;

@Data
public class EvaluationRequest {
    private String specialistId;
    private String entrepreneurId;
    private double score;          // 0.0 - 5.0
    private String comment;
    private LocalTime startTime;
    private LocalTime endTime;
    private Date availableDate;
}