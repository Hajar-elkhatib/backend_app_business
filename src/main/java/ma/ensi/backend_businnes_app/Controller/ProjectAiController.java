package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.DTOS.request.BusinessValidationRequest;
import ma.ensi.backend_businnes_app.DTOS.request.MarketFeedbackRequest;
import ma.ensi.backend_businnes_app.Model.analysis.BusinessIdeaAnalysis;
import ma.ensi.backend_businnes_app.Model.analysis.MarketFeedback;
import ma.ensi.backend_businnes_app.Service.AnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectAiController {

    private final AnalysisService analysisService;

    public ProjectAiController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/{projectId}/analysis/run")
    public ResponseEntity<BusinessIdeaAnalysis> runAnalysis(
            @PathVariable String projectId,
            @RequestBody(required = false) BusinessValidationRequest request) {
        return ResponseEntity.ok(analysisService.analyzeProject(projectId, request));
    }

    @GetMapping("/{projectId}/analysis/latest")
    public ResponseEntity<BusinessIdeaAnalysis> latestAnalysis(@PathVariable String projectId) {
        return ResponseEntity.ok(analysisService.getLatestAnalysis(projectId));
    }

    @GetMapping("/{projectId}/analysis/history")
    public ResponseEntity<List<BusinessIdeaAnalysis>> analysisHistory(@PathVariable String projectId) {
        return ResponseEntity.ok(analysisService.getAnalysisHistory(projectId));
    }

    @PostMapping("/{projectId}/feedbacks")
    public ResponseEntity<List<MarketFeedback>> createFeedbacks(
            @PathVariable String projectId,
            @RequestBody MarketFeedbackRequest request) {
        List<String> texts = request.getFeedbacks() != null
                ? request.getFeedbacks()
                : Arrays.asList((request.getFeedbackText() == null ? "" : request.getFeedbackText()).split("\\r?\\n"));
        return ResponseEntity.ok(analysisService.createFeedbacks(projectId, texts));
    }

    @GetMapping("/{projectId}/feedbacks")
    public ResponseEntity<List<MarketFeedback>> getFeedbacks(@PathVariable String projectId) {
        return ResponseEntity.ok(analysisService.getFeedbacks(projectId));
    }

    @PostMapping("/{projectId}/feedbacks/analyze")
    public ResponseEntity<List<MarketFeedback>> analyzeFeedbacks(@PathVariable String projectId) {
        return ResponseEntity.ok(analysisService.analyzeFeedbacks(projectId));
    }
}
