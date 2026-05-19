package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class RegisterEntrepreneurRequest {
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private String companyName;
    private String businessType;
}