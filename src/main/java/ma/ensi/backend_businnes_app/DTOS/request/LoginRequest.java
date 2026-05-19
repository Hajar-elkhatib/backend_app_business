package ma.ensi.backend_businnes_app.DTOS.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}