package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.Service.AuthService;
import ma.ensi.backend_businnes_app.DTOS.request.RegisterEntrepreneurRequest;
import ma.ensi.backend_businnes_app.DTOS.request.RegisterSpecialistRequest;
import ma.ensi.backend_businnes_app.DTOS.request.LoginRequest;
import ma.ensi.backend_businnes_app.DTOS.response.AuthResponse;
import ma.ensi.backend_businnes_app.DTOS.response.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/entrepreneur")
    public ResponseEntity<AuthResponse> registerEntrepreneur(
            @RequestBody RegisterEntrepreneurRequest request) {
        AuthResponse response = authService.registerEntrepreneur(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/register/specialist")
    public ResponseEntity<AuthResponse> registerSpecialist(
            @RequestBody RegisterSpecialistRequest request) {
        AuthResponse response = authService.registerSpecialist(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // JWT is stateless — frontend deletes token
        // Backend just confirms
        return ResponseEntity.ok("Logged out successfully");
    }

}