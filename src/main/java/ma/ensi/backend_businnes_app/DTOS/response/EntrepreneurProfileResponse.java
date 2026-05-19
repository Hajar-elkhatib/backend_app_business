package ma.ensi.backend_businnes_app.DTOS.response;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntrepreneurProfileResponse {
    private String id;
    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String companyName;
    private String businessType;
}
