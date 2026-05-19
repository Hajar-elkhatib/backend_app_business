package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class UpdateEntrepreneurRequest {
    private String fullName;
    private String phone;
    private String companyName;
    private String businessType;
}
