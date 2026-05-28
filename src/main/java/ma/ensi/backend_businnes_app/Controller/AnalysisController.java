package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.Model.analysis.*;
import ma.ensi.backend_businnes_app.Model.social.SpecialistRecommendation;
import ma.ensi.backend_businnes_app.DTOS.request.BusinessValidationRequest;
import ma.ensi.backend_businnes_app.DTOS.response.StartupSuccessResponse;
import ma.ensi.backend_businnes_app.Service.AnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    // ✅ Full Business Validation
    @PostMapping("/{projectId}/analyze")
    public ResponseEntity<BusinessIdeaAnalysis> analyzeProject(
            @PathVariable String projectId,
            @RequestBody(required = false) BusinessValidationRequest request) {
        return ResponseEntity.ok(
                analysisService.analyzeProject(projectId, request));
    }

    // ✅ Startup Success
    @PostMapping("/{projectId}/startup-success")
    public ResponseEntity<StartupSuccessResponse> predictSuccess(
            @PathVariable String projectId) {
        return ResponseEntity.ok(
                analysisService.predictStartupSuccess(projectId));
    }

    // ✅ Sentiment Analysis
    @PostMapping("/{projectId}/sentiment")
    public ResponseEntity<SentimentAnalysis> analyzeSentiment(
            @PathVariable String projectId,
            @RequestParam String text,
            @RequestParam String textSource) {
        return ResponseEntity.ok(
                analysisService.analyzeSentiment(projectId, text, textSource));
    }

    // ✅ Market Analysis
    @PostMapping("/{projectId}/market")
    public ResponseEntity<MarketAnalysis> analyzeMarket(
            @PathVariable String projectId) {
        return ResponseEntity.ok(
                analysisService.analyzeMarket(projectId));
    }

    // ✅ Specialist Recommendation
    @PostMapping("/{projectId}/specialists/recommend")
    public ResponseEntity<List<SpecialistRecommendation>> recommendSpecialists(
            @PathVariable String projectId) {
        return ResponseEntity.ok(
                analysisService.recommendSpecialists(projectId));
    }
}
