package ma.ensi.backend_businnes_app.Model.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "entrepreneurs")
public class Entrepreneur {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;
    private String companyName;
    private String businessType;

    private Date createdAt;
}