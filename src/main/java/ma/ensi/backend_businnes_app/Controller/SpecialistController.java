package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.DTOS.request.UpdateSpecialistRequest;
import ma.ensi.backend_businnes_app.DTOS.response.SpecialistProfileResponse;
import ma.ensi.backend_businnes_app.Service.SpecialistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialists")
@CrossOrigin(origins = "*")
public class SpecialistController {

    private final SpecialistService specialistService;

    public SpecialistController(SpecialistService specialistService) {
        this.specialistService = specialistService;
    }


    @GetMapping
    public ResponseEntity<List<SpecialistProfileResponse>> getAllSpecialists() {
        return ResponseEntity.ok(specialistService.getAllSpecialists());
    }


    @GetMapping("/{userId}/profile")
    public ResponseEntity<SpecialistProfileResponse> getProfile(
            @PathVariable String userId) {
        return ResponseEntity.ok(specialistService.getProfile(userId));
    }


    @GetMapping("/availability/{status}")
    public ResponseEntity<List<SpecialistProfileResponse>> getByAvailability(
            @PathVariable String status) {
        return ResponseEntity.ok(specialistService.getByAvailability(status));
    }


    @GetMapping("/sector/{sector}")
    public ResponseEntity<List<SpecialistProfileResponse>> getBySector(
            @PathVariable String sector) {
        return ResponseEntity.ok(specialistService.getBySector(sector));
    }


    @GetMapping("/skill/{skill}")
    public ResponseEntity<List<SpecialistProfileResponse>> getBySkill(
            @PathVariable String skill) {
        return ResponseEntity.ok(specialistService.getBySkill(skill));
    }


    @PutMapping("/{userId}/profile")
    public ResponseEntity<SpecialistProfileResponse> updateProfile(
            @PathVariable String userId,
            @RequestBody UpdateSpecialistRequest request) {
        return ResponseEntity.ok(specialistService.updateProfile(userId, request));
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteSpecialist(
            @PathVariable String userId) {
        specialistService.deleteSpecialist(userId);
        return ResponseEntity.ok("Specialist deleted successfully");
    }
}