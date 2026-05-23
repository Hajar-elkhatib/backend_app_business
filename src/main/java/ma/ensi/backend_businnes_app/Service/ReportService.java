package ma.ensi.backend_businnes_app.Service;

import ma.ensi.backend_businnes_app.Model.core.Report;
import ma.ensi.backend_businnes_app.Repository.core.ReportRepository;
import ma.ensi.backend_businnes_app.DTOS.request.ReportRequest;
import ma.ensi.backend_businnes_app.DTOS.response.ReportResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;

    // ✅ Upload folder — change this to your actual path
    private final String uploadDir = "uploads/reports/";

    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    // ✅ Create Report after analysis
    public ReportResponse createReport(ReportRequest request) {
        Report report = new Report();
        report.setProjectId(request.getProjectId());
        report.setTitle(request.getTitle());
        report.setSummary(request.getSummary());
        report.setReportType(request.getReportType());
        report.setRegion(request.getRegion());
        report.setModelVersion(request.getModelVersion());
        report.setCreatedAt(new Date());
        Report saved = reportRepository.save(report);
        return mapToResponse(saved);
    }

    // ✅ Upload PDF report
    public ReportResponse uploadReport(String reportId,
                                       MultipartFile file) throws IOException {

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        // Create uploads directory if not exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);

        // Save file to disk
        Files.copy(file.getInputStream(), filePath);

        // Save PDF URL in MongoDB
        report.setPdfUrl(uploadDir + filename);
        Report saved = reportRepository.save(report);

        return mapToResponse(saved);
    }

    // ✅ Get Report by Project
    public List<ReportResponse> getReportsByProject(String projectId) {
        return reportRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ✅ Get Latest Report by Project
    public ReportResponse getLatestReport(String projectId) {
        Report report = reportRepository
                .findFirstByProjectIdOrderByCreatedAtDesc(projectId)
                .orElseThrow(() -> new RuntimeException("No report found"));
        return mapToResponse(report);
    }

    // ✅ Get Report by ID
    public ReportResponse getReportById(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return mapToResponse(report);
    }

    // ✅ Delete Report
    public void deleteReport(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        reportRepository.delete(report);
    }

    // ✅ Helper
    private ReportResponse mapToResponse(Report report) {
        ReportResponse response = new ReportResponse();
        response.setId(report.getId());
        response.setProjectId(report.getProjectId());
        response.setTitle(report.getTitle());
        response.setSummary(report.getSummary());
        response.setReportType(report.getReportType());
        response.setPdfUrl(report.getPdfUrl());
        response.setRegion(report.getRegion());
        response.setModelVersion(report.getModelVersion());
        response.setCreatedAt(report.getCreatedAt());
        return response;
    }
}