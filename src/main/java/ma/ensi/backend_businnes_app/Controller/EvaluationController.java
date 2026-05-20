package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.DTOS.request.EvaluationRequest;
import ma.ensi.backend_businnes_app.DTOS.response.EvaluationResponse;
import ma.ensi.backend_businnes_app.Service.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@CrossOrigin(origins = "*")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    //  Create Evaluation
    @PostMapping
    public ResponseEntity<EvaluationResponse> createEvaluation(
            @RequestBody EvaluationRequest request) {
        return ResponseEntity.ok(evaluationService.createEvaluation(request));
    }

    // Get All Evaluations for Specialist
    @GetMapping("/specialist/{specialistId}")
    public ResponseEntity<List<EvaluationResponse>> getBySpecialist(
            @PathVariable String specialistId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsBySpecialist(specialistId));
    }

    // Get All Evaluations by Entrepreneur
    @GetMapping("/entrepreneur/{entrepreneurId}")
    public ResponseEntity<List<EvaluationResponse>> getByEntrepreneur(
            @PathVariable String entrepreneurId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByEntrepreneur(entrepreneurId));
    }

    // Get Single Evaluation
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationResponse> getById(
            @PathVariable String id) {
        return ResponseEntity.ok(evaluationService.getEvaluationById(id));
    }

    //  Delete Evaluation
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvaluation(
            @PathVariable String id) {
        evaluationService.deleteEvaluation(id);
        return ResponseEntity.ok("Evaluation deleted successfully");
    }
}