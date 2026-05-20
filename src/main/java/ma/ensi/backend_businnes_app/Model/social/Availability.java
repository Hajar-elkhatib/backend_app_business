package ma.ensi.backend_businnes_app.Model.social;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "availabilities")
public class Availability {

    @Id
    private String id;

    @Indexed
    private String specialistId;   // → Specialist._id

    private Date availableDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;         // "OPEN" | "BOOKED" | "CANCELLED"
    private int maxSessions;

    private Date createdAt;
}