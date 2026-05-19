package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.DTOS.request.UpdateSpecialistRequest;
import ma.ensi.backend_businnes_app.DTOS.response.SpecialistProfileResponse;
import ma.ensi.backend_businnes_app.Service.SpecialistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/specialists")
@CrossOrigin(origins = "*")
public class SpecialistController {

    private final SpecialistService specialistService;

    public SpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }


    @GetMapping("/{userId}/profile")
    public ResponseEntity<SpecialistProfileResponse> getProfile(
            @PathVariable String userId) {
        return ResponseEntity.ok(specialistService.getProfile(userId));
    }


    @PutMapping("/{userId}/profile")
    public ResponseEntity<SpecialistProfileResponse> updateProfile(
            @PathVariable String userId,
            @RequestBody UpdateSpecialistRequest request) {
        return ResponseEntity.ok(specialistService.updateProfile(userId, request));
    }
}