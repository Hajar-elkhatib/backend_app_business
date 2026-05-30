package ma.ensi.backend_businnes_app.Service;

import ma.ensi.backend_businnes_app.Model.core.Report;
import ma.ensi.backend_businnes_app.Model.core.Project;
import ma.ensi.backend_businnes_app.Model.analysis.BusinessIdeaAnalysis;
import ma.ensi.backend_businnes_app.Repository.core.ProjectRepository;
import ma.ensi.backend_businnes_app.Repository.analysis.BusinessIdeaAnalysisRepository;
import ma.ensi.backend_businnes_app.Repository.core.ReportRepository;
import ma.ensi.backend_businnes_app.DTOS.request.ReportRequest;
import ma.ensi.backend_businnes_app.DTOS.response.ReportResponse;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ProjectRepository projectRepository;
    private final BusinessIdeaAnalysisRepository businessIdeaAnalysisRepository;
    private final RestTemplate restTemplate;
    private final String fastApiBaseUrl;

    // ✅ Upload folder — change this to your actual path
    private final String uploadDir = "uploads/reports/";

    public ReportService(
            ReportRepository reportRepository,
            ProjectRepository projectRepository,
            BusinessIdeaAnalysisRepository businessIdeaAnalysisRepository,
            RestTemplate restTemplate,
            @Value("${ai.fastapi.base-url:${fastapi.base-url}}") String fastApiBaseUrl) {
        this.reportRepository = reportRepository;
        this.projectRepository = projectRepository;
        this.businessIdeaAnalysisRepository = businessIdeaAnalysisRepository;
        this.restTemplate = restTemplate;
        this.fastApiBaseUrl = trimTrailingSlash(fastApiBaseUrl);
    }

    // ✅ Create Report after analysis
    public ReportResponse createReport(ReportRequest request) {
        Report report = new Report();
        report.setProjectId(request.getProjectId());
        report.setTitle(request.getTitle());
        report.setSummary(request.getSummary());
        report.setReportType(request.getReportType());
        report.setContent(request.getContent());
        report.setGeneratedBy(request.getGeneratedBy());
        report.setRegion(request.getRegion());
        report.setModelVersion(request.getModelVersion());
        report.setCreatedAt(new Date());
        Report saved = reportRepository.save(report);
        return mapToResponse(saved);
    }

    @SuppressWarnings("unchecked")
    public ReportResponse generateProjectReport(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        BusinessIdeaAnalysis analysis = businessIdeaAnalysisRepository.findFirstByProjectIdOrderByCreatedAtDesc(projectId)
                .orElseThrow(() -> new RuntimeException("Run analysis before generating a report"));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("projectData", project);
        payload.put("analysisResult", analysis);
        payload.put("includeBusinessPlan", true);

        Map<String, Object> generated = restTemplate.postForObject(
                fastApiBaseUrl + "/api/v1/reports/generate-content",
                payload,
                Map.class
        );
        if (generated == null) {
            generated = Map.of();
        }

        Report report = new Report();
        report.setProjectId(projectId);
        report.setAnalysisId(analysis.getId());
        report.setTitle(text(generated, "title", "Business validation report - " + project.getTitle()));
        report.setSummary(text(generated, "executiveSummary", text(generated, "analysisSummary", "")));
        report.setReportType("AI_GENERATED");
        report.setContent(toReportContent(generated));
        report.setGeneratedBy(text(generated, "generationSource", "AI"));
        report.setModelVersion(analysis.getModelVersion());
        report.setCreatedAt(new Date());

        Report saved = reportRepository.save(report);
        saved.setPdfUrl(writePdf(saved));
        saved = reportRepository.save(saved);
        return mapToResponse(saved);
    }

    public byte[] downloadReport(String reportId) throws IOException {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        if (report.getPdfUrl() == null || report.getPdfUrl().isBlank()) {
            report.setPdfUrl(writePdf(report));
            reportRepository.save(report);
        }
        return Files.readAllBytes(Paths.get(report.getPdfUrl()));
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
        response.setAnalysisId(report.getAnalysisId());
        response.setTitle(report.getTitle());
        response.setSummary(report.getSummary());
        response.setReportType(report.getReportType());
        response.setContent(report.getContent());
        response.setPdfUrl(report.getPdfUrl());
        response.setGeneratedBy(report.getGeneratedBy());
        response.setRegion(report.getRegion());
        response.setModelVersion(report.getModelVersion());
        response.setCreatedAt(report.getCreatedAt());
        return response;
    }

    private String toReportContent(Map<String, Object> generated) {
        return """
                Executive Summary
                %s

                Analysis
                %s

                Strengths
                %s

                Weaknesses
                %s

                Recommendations
                %s

                Business Plan
                %s

                Warnings
                %s
                """.formatted(
                text(generated, "executiveSummary", ""),
                text(generated, "analysisSummary", ""),
                String.valueOf(generated.getOrDefault("strengths", List.of())),
                String.valueOf(generated.getOrDefault("weaknesses", List.of())),
                String.valueOf(generated.getOrDefault("recommendations", List.of())),
                String.valueOf(generated.getOrDefault("businessPlan", Map.of())),
                String.valueOf(generated.getOrDefault("warnings", List.of()))
        );
    }

    private String writePdf(Report report) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String filename = "report-" + report.getId() + ".pdf";
            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, minimalPdf(report).getBytes(StandardCharsets.ISO_8859_1));
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Report PDF could not be generated", e);
        }
    }

    private String minimalPdf(Report report) {
        String text = (report.getTitle() + "\n\n" + report.getSummary() + "\n\n" + report.getContent())
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("\r", "")
                .replace("\n", ") Tj T* (");
        return "%PDF-1.4\n" +
                "1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n" +
                "2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n" +
                "3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >> endobj\n" +
                "4 0 obj << /Length " + (text.length() + 80) + " >> stream\n" +
                "BT /F1 11 Tf 50 742 Td 14 TL (" + text + ") Tj ET\n" +
                "endstream endobj\n" +
                "5 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> endobj\n" +
                "xref\n0 6\n0000000000 65535 f \n0000000010 00000 n \n0000000060 00000 n \n0000000117 00000 n \n0000000241 00000 n \n0000000000 00000 n \ntrailer << /Root 1 0 R /Size 6 >>\nstartxref\n0\n%%EOF";
    }

    private String text(Map<String, Object> source, String key, String defaultValue) {
        Object value = source.get(key);
        return value == null ? defaultValue : String.valueOf(value);
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
