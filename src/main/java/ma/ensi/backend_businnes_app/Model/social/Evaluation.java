package ma.ensi.backend_businnes_app.Model.social;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.Date;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "evaluations")
public class Evaluation {

    @Id
    private String id;

    @Indexed
    private String specialistId;   // → Specialist._id

    @Indexed
    private String entrepreneurId; // → Entrepreneur._id

    private LocalTime startTime;
    private LocalTime endTime;
    private double score;
    private String comment;
    private String status;         // "PENDING" | "COMPLETED"
    private int currentSessions;

    private Date availableDate;
}