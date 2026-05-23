package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.DTOS.request.ReportRequest;
import ma.ensi.backend_businnes_app.DTOS.response.ReportResponse;
import ma.ensi.backend_businnes_app.Service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // ✅ Create Report
    @PostMapping
    public ResponseEntity<ReportResponse> createReport(
            @RequestBody ReportRequest request) {
        return ResponseEntity.ok(reportService.createReport(request));
    }

    // ✅ Upload PDF
    @PostMapping("/{reportId}/upload")
    public ResponseEntity<ReportResponse> uploadPdf(
            @PathVariable String reportId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(reportService.uploadReport(reportId, file));
    }

    // ✅ Get Reports by Project
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ReportResponse>> getByProject(
            @PathVariable String projectId) {
        return ResponseEntity.ok(reportService.getReportsByProject(projectId));
    }

    // ✅ Get Latest Report
    @GetMapping("/project/{projectId}/latest")
    public ResponseEntity<ReportResponse> getLatest(
            @PathVariable String projectId) {
        return ResponseEntity.ok(reportService.getLatestReport(projectId));
    }

    // ✅ Get by ID
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportResponse> getById(
            @PathVariable String reportId) {
        return ResponseEntity.ok(reportService.getReportById(reportId));
    }

    // ✅ Delete
    @DeleteMapping("/{reportId}")
    public ResponseEntity<String> deleteReport(
            @PathVariable String reportId) {
        reportService.deleteReport(reportId);
        return ResponseEntity.ok("Report deleted successfully");
    }
}