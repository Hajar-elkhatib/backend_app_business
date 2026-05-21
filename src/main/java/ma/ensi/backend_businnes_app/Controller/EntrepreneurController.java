package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.DTOS.request.UpdateEntrepreneurRequest;
import ma.ensi.backend_businnes_app.DTOS.response.EntrepreneurProfileResponse;
import ma.ensi.backend_businnes_app.Service.EntrepreneurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/entrepreneurs")
@CrossOrigin(origins = "*")
public class EntrepreneurController {

    private final EntrepreneurService entrepreneurService;

    public EntrepreneurController(EntrepreneurService entrepreneurService) {
        this.entrepreneurService = entrepreneurService;
    }


    @GetMapping
    public ResponseEntity<List<EntrepreneurProfileResponse>> getAllEntrepreneurs() {
        return ResponseEntity.ok(entrepreneurService.getAllEntrepreneurs());
    }


    @GetMapping("/{userId}/profile")
    public ResponseEntity<EntrepreneurProfileResponse> getProfile(
            @PathVariable String userId) {
        return ResponseEntity.ok(entrepreneurService.getProfile(userId));
    }


    @PutMapping("/{userId}/profile")
    public ResponseEntity<EntrepreneurProfileResponse> updateProfile(
            @PathVariable String userId,
            @RequestBody UpdateEntrepreneurRequest request) {
        return ResponseEntity.ok(entrepreneurService.updateProfile(userId, request));
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteEntrepreneur(
            @PathVariable String userId) {
        entrepreneurService.deleteEntrepreneur(userId);
        return ResponseEntity.ok("Entrepreneur deleted successfully");
    }
}