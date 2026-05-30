package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.DTOS.response.ReportResponse;
import ma.ensi.backend_businnes_app.Service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectReportController {

    private final ReportService reportService;

    public ProjectReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/{projectId}/reports/generate")
    public ResponseEntity<ReportResponse> generate(@PathVariable String projectId) {
        return ResponseEntity.ok(reportService.generateProjectReport(projectId));
    }

    @GetMapping("/{projectId}/reports")
    public ResponseEntity<List<ReportResponse>> list(@PathVariable String projectId) {
        return ResponseEntity.ok(reportService.getReportsByProject(projectId));
    }
}
