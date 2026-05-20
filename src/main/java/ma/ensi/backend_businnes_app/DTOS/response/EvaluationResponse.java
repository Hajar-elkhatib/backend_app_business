package ma.ensi.backend_businnes_app.DTOS.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationResponse {
    private String id;
    private String specialistId;
    private String entrepreneurId;
    private double score;
    private String comment;
    private String status;
    private LocalTime startTime;
    private LocalTime endTime;
    private Date availableDate;
    private int currentSessions;
}