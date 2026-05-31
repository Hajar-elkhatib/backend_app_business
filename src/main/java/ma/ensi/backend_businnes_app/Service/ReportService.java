package ma.ensi.backend_businnes_app.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.ensi.backend_businnes_app.DTOS.request.ReportRequest;
import ma.ensi.backend_businnes_app.DTOS.response.ReportResponse;
import ma.ensi.backend_businnes_app.Model.analysis.BusinessIdeaAnalysis;
import ma.ensi.backend_businnes_app.Model.core.Project;
import ma.ensi.backend_businnes_app.Model.core.Report;
import ma.ensi.backend_businnes_app.Repository.analysis.BusinessIdeaAnalysisRepository;
import ma.ensi.backend_businnes_app.Repository.core.ProjectRepository;
import ma.ensi.backend_businnes_app.Repository.core.ReportRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final ProjectRepository projectRepository;
    private final BusinessIdeaAnalysisRepository businessIdeaAnalysisRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String fastApiBaseUrl;
    private final String uploadDir = "uploads/reports/";

    public ReportService(
            ReportRepository reportRepository,
            ProjectRepository projectRepository,
            BusinessIdeaAnalysisRepository businessIdeaAnalysisRepository,
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${ai.fastapi.base-url:${fastapi.base-url}}") String fastApiBaseUrl) {
        this.reportRepository = reportRepository;
        this.projectRepository = projectRepository;
        this.businessIdeaAnalysisRepository = businessIdeaAnalysisRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.fastApiBaseUrl = trimTrailingSlash(fastApiBaseUrl);
    }

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
        report.setTitle(cleanText(text(generated, "title", "Business Validation Report")));
        report.setSummary(cleanText(text(generated, "executiveSummary", "")));
        report.setReportType("AI_GENERATED");
        report.setContent(toJson(generated));
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

    public String getDownloadFilename(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        if (report.getPdfUrl() != null && !report.getPdfUrl().isBlank()) {
            return Paths.get(report.getPdfUrl()).getFileName().toString();
        }
        return "business-validation-report-" + slug(report.getTitle()) + ".pdf";
    }

    public ReportResponse uploadReport(String reportId, MultipartFile file) throws IOException {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        report.setPdfUrl(uploadDir + filename);
        Report saved = reportRepository.save(report);
        return mapToResponse(saved);
    }

    public List<ReportResponse> getReportsByProject(String projectId) {
        return reportRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReportResponse getLatestReport(String projectId) {
        Report report = reportRepository
                .findFirstByProjectIdOrderByCreatedAtDesc(projectId)
                .orElseThrow(() -> new RuntimeException("No report found"));
        return mapToResponse(report);
    }

    public ReportResponse getReportById(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return mapToResponse(report);
    }

    public void deleteReport(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        reportRepository.delete(report);
    }

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

    private String writePdf(Report report) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            ReportDocument document = parseReportDocument(report);
            String filename = "business-validation-report-" + slug(document.projectName()) + "-"
                    + new SimpleDateFormat("yyyyMMdd").format(report.getCreatedAt() != null ? report.getCreatedAt() : new Date())
                    + ".pdf";
            Path filePath = uploadPath.resolve(filename);
            Files.write(filePath, buildPdf(document, report));
            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Report PDF could not be generated", e);
        }
    }

    @SuppressWarnings("unchecked")
    private ReportDocument parseReportDocument(Report report) {
        Map<String, Object> root = new LinkedHashMap<>();
        try {
            if (report.getContent() != null && report.getContent().trim().startsWith("{")) {
                root = objectMapper.readValue(report.getContent(), new TypeReference<>() {});
            }
        } catch (IOException ignored) {
            root = new LinkedHashMap<>();
        }

        Map<String, Object> plan = map(root.get("businessPlan"));
        Map<String, Object> analysis = map(root.get("analysis"));

        if (analysis.isEmpty()) {
            analysis.put("finalScore", null);
            analysis.put("startupSuccessScore", null);
            analysis.put("marketAnalysisScore", null);
            analysis.put("marketOpinionScore", null);
            analysis.put("predictionLabel", "");
            analysis.put("successProbability", null);
            analysis.put("strengths", List.of());
            analysis.put("weaknesses", List.of());
            analysis.put("recommendations", List.of());
            analysis.put("recommendedSpecialists", List.of());
            analysis.put("warnings", List.of());
        }

        return new ReportDocument(
                text(root, "title", "Business Validation Report"),
                text(root, "projectName", "Selected project"),
                text(root, "executiveSummary", report.getSummary()),
                plan,
                analysis
        );
    }

    private byte[] buildPdf(ReportDocument document, Report report) throws IOException {
        String page1 = renderBusinessPlanPage(document, report);
        String page2 = renderAnalysisPage(document);

        List<byte[]> objects = new ArrayList<>();
        objects.add(pdfObject("<< /Type /Catalog /Pages 2 0 R >>"));
        objects.add(pdfObject("<< /Type /Pages /Kids [3 0 R 5 0 R] /Count 2 >>"));
        objects.add(pdfObject("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R /Resources << /Font << /F1 7 0 R /F2 8 0 R /F3 9 0 R >> >> >>"));
        objects.add(pdfStream(page1));
        objects.add(pdfObject("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 6 0 R /Resources << /Font << /F1 7 0 R /F2 8 0 R /F3 9 0 R >> >> >>"));
        objects.add(pdfStream(page2));
        objects.add(pdfObject("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>"));
        objects.add(pdfObject("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>"));
        objects.add(pdfObject("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Oblique >>"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write("%PDF-1.4\n".getBytes(StandardCharsets.ISO_8859_1));
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(out.size());
            out.write(("%d 0 obj\n".formatted(i + 1)).getBytes(StandardCharsets.ISO_8859_1));
            out.write(objects.get(i));
            out.write("\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));
        }
        int xref = out.size();
        out.write(("xref\n0 " + (objects.size() + 1) + "\n").getBytes(StandardCharsets.ISO_8859_1));
        out.write("0000000000 65535 f \n".getBytes(StandardCharsets.ISO_8859_1));
        for (Integer offset : offsets) {
            out.write(String.format(Locale.US, "%010d 00000 n \n", offset).getBytes(StandardCharsets.ISO_8859_1));
        }
        out.write(("trailer << /Root 1 0 R /Size " + (objects.size() + 1) + " >>\nstartxref\n" + xref + "\n%%EOF")
                .getBytes(StandardCharsets.ISO_8859_1));
        return out.toByteArray();
    }

    private String renderBusinessPlanPage(ReportDocument doc, Report report) {
        PdfCanvas pdf = new PdfCanvas();
        pdf.background();
        pdf.header("NexusAI", doc.title(), doc.projectName());
        pdf.text(420, 710, "Generated " + date(report.getCreatedAt()), "F1", 9, 0.36, 0.38, 0.45);
        pdf.badge(420, 682, statusLabel(number(map(doc.analysis()).get("finalScore"))));

        pdf.sectionTitle(48, 642, "Executive Summary");
        pdf.wrappedText(48, 620, doc.executiveSummary(), 88, 4, 10.5, 13);

        double leftX = 48;
        double rightX = 314;
        double y = 540;
        y = pdf.planCard(leftX, y, "Problem & Solution",
                text(doc.businessPlan(), "problem", "Insufficient data available."),
                text(doc.businessPlan(), "solution", "Insufficient data available."));
        y = pdf.planCard(leftX, y, "Target Customers",
                text(doc.businessPlan(), "targetCustomers", "Insufficient data available."), null);
        pdf.planCard(leftX, y, "Value Proposition",
                text(doc.businessPlan(), "valueProposition", "Insufficient data available."), null);

        double rightY = 540;
        rightY = pdf.planCard(rightX, rightY, "Business Model",
                text(doc.businessPlan(), "businessModel", "Insufficient data available."), null);
        rightY = pdf.planCard(rightX, rightY, "Marketing Strategy",
                text(doc.businessPlan(), "marketingStrategy", "Insufficient data available."), null);
        pdf.bulletCard(rightX, rightY, "Priority Next Steps", list(doc.businessPlan().get("nextSteps")), 4);
        pdf.footer(1);
        return pdf.content();
    }

    private String renderAnalysisPage(ReportDocument doc) {
        Map<String, Object> analysis = doc.analysis();
        PdfCanvas pdf = new PdfCanvas();
        pdf.background();
        pdf.header("NexusAI", "AI Validation Analysis", doc.projectName());

        double y = 650;
        pdf.scoreCard(48, y, "Final Validation Score", number(analysis.get("finalScore")));
        pdf.scoreCard(181, y, "Startup Success", number(analysis.get("startupSuccessScore")));
        pdf.scoreCard(314, y, "Market Analysis", number(analysis.get("marketAnalysisScore")));
        pdf.scoreCard(447, y, "Market Opinion", number(analysis.get("marketOpinionScore")));

        pdf.sectionTitle(48, 572, "Prediction Status");
        String prediction = text(analysis, "predictionLabel", "Not available");
        String probability = score(number(analysis.get("successProbability")));
        String interpretation = text(analysis, "interpretation", "The analysis summarizes available model signals and business evidence.");
        pdf.wrappedText(48, 550, "Prediction Label: " + prediction + "   |   Success Probability: " + probability, 92, 1, 10.5, 13);
        pdf.wrappedText(48, 530, interpretation, 92, 3, 10, 12);

        pdf.bulletCard(48, 465, "Strengths", list(analysis.get("strengths")), 4);
        pdf.bulletCard(314, 465, "Weaknesses", list(analysis.get("weaknesses")), 4);
        pdf.bulletCard(48, 270, "Recommendations", list(analysis.get("recommendations")), 4);

        List<Object> specialists = list(analysis.get("recommendedSpecialists"));
        if (!specialists.isEmpty()) {
            pdf.specialistCard(314, 270, "Recommended Specialists", specialists);
        }
        List<Object> warnings = list(analysis.get("warnings"));
        if (!warnings.isEmpty()) {
            pdf.warningLine(48, 92, warnings);
        }
        pdf.footer(2);
        return pdf.content();
    }

    private byte[] pdfObject(String value) {
        return value.getBytes(StandardCharsets.ISO_8859_1);
    }

    private byte[] pdfStream(String stream) {
        byte[] bytes = stream.getBytes(StandardCharsets.ISO_8859_1);
        return ("<< /Length " + bytes.length + " >>\nstream\n" + stream + "\nendstream")
                .getBytes(StandardCharsets.ISO_8859_1);
    }

    private String toJson(Map<String, Object> generated) {
        try {
            return objectMapper.writeValueAsString(generated);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private String text(Map<String, Object> source, String key, String defaultValue) {
        Object value = source.get(key);
        if (value == null) {
            return defaultValue == null ? "" : defaultValue;
        }
        if (value instanceof Map<?, ?> || value instanceof List<?>) {
            return defaultValue == null ? "" : defaultValue;
        }
        String text = cleanText(String.valueOf(value));
        return text.isBlank() ? (defaultValue == null ? "" : defaultValue) : text;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> map(Object value) {
        return value instanceof Map<?, ?> ? (Map<String, Object>) value : new LinkedHashMap<>();
    }

    private List<Object> list(Object value) {
        if (value instanceof List<?> raw) {
            return raw.stream().filter(item -> item != null && !String.valueOf(item).isBlank()).limit(6).collect(Collectors.toList());
        }
        if (value == null || String.valueOf(value).isBlank()) {
            return List.of();
        }
        return List.of(value);
    }

    private Double number(Object value) {
        if (value == null || String.valueOf(value).isBlank()) {
            return null;
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String score(Double value) {
        return value == null ? "Not available" : String.format(Locale.US, "%.1f%%", value);
    }

    private String statusLabel(Double score) {
        if (score == null) return "Needs Validation";
        if (score >= 70) return "Promising";
        if (score >= 50) return "Needs Validation";
        return "High Risk";
    }

    private String date(Date date) {
        return new SimpleDateFormat("MMM d, yyyy", Locale.US).format(date != null ? date : new Date());
    }

    private String slug(String value) {
        String cleaned = cleanText(value).toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "-");
        cleaned = cleaned.replaceAll("(^-|-$)", "");
        return cleaned.isBlank() ? "project" : cleaned;
    }

    private String cleanText(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace("’", "'")
                .replace("“", "\"")
                .replace("”", "\"")
                .replace("–", "-")
                .replace("—", "-")
                .replaceAll("[^\\x20-\\x7E\\n]", "");
        return normalized.trim();
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }

    private record ReportDocument(
            String title,
            String projectName,
            String executiveSummary,
            Map<String, Object> businessPlan,
            Map<String, Object> analysis
    ) {}

    private final class PdfCanvas {
        private final StringBuilder body = new StringBuilder();

        String content() {
            return body.toString();
        }

        void background() {
            fill(0, 0, 612, 792, 1, 1, 1);
            fill(0, 748, 612, 44, 0.96, 0.97, 1);
            fill(0, 744, 612, 4, 0.31, 0.28, 0.90);
        }

        void header(String brand, String title, String projectName) {
            text(48, 762, brand, "F2", 15, 0.08, 0.09, 0.12);
            text(48, 724, title, "F2", 24, 0.08, 0.09, 0.12);
            text(48, 704, projectName, "F1", 11, 0.36, 0.38, 0.45);
        }

        void sectionTitle(double x, double y, String title) {
            text(x, y, title, "F2", 14, 0.08, 0.09, 0.12);
            fill(x, y - 7, 36, 2, 0.31, 0.28, 0.90);
        }

        double planCard(double x, double y, String title, String first, String second) {
            double height = second == null ? 112 : 140;
            card(x, y - height + 18, 218, height);
            text(x + 14, y - 8, title, "F2", 11, 0.08, 0.09, 0.12);
            double current = wrappedText(x + 14, y - 28, first, 32, second == null ? 5 : 3, 9.2, 11);
            if (second != null) {
                wrappedText(x + 14, current - 6, second, 32, 3, 9.2, 11);
            }
            return y - height - 10;
        }

        void bulletCard(double x, double y, String title, List<Object> items, int maxItems) {
            card(x, y - 162, 218, 180);
            text(x + 14, y - 8, title, "F2", 11, 0.08, 0.09, 0.12);
            List<Object> safeItems = items.isEmpty() ? List.of("Insufficient data available.") : items;
            double current = y - 30;
            int count = 0;
            for (Object item : safeItems) {
                if (count++ >= maxItems) break;
                text(x + 14, current, "-", "F2", 9, 0.31, 0.28, 0.90);
                current = wrappedText(x + 26, current, itemText(item), 29, 2, 8.8, 10.5) - 4;
            }
        }

        void specialistCard(double x, double y, String title, List<Object> specialists) {
            card(x, y - 162, 218, 180);
            text(x + 14, y - 8, title, "F2", 11, 0.08, 0.09, 0.12);
            double current = y - 30;
            int count = 0;
            for (Object specialist : specialists) {
                if (count++ >= 3) break;
                Map<String, Object> item = map(specialist);
                String name = fieldText(item, "specialistName", fieldText(item, "full_name", fieldText(item, "fullName", "Recommended specialist")));
                String domain = fieldText(item, "expertiseDomain", fieldText(item, "expertise_domain", fieldText(item, "domain", "")));
                String reason = fieldText(item, "reason", "Recommended for this project's needs.");
                text(x + 14, current, name, "F2", 8.8, 0.08, 0.09, 0.12);
                current = wrappedText(x + 14, current - 11, (domain.isBlank() ? "" : domain + " - ") + reason, 31, 2, 8.2, 9.5) - 4;
            }
        }

        void scoreCard(double x, double y, String label, Double value) {
            card(x, y - 58, 112, 76);
            text(x + 10, y - 4, label, "F2", 8, 0.36, 0.38, 0.45);
            text(x + 10, y - 30, score(value), "F2", 18, 0.31, 0.28, 0.90);
        }

        void warningLine(double x, double y, List<Object> warnings) {
            text(x, y, "Limitations / Warnings", "F2", 11, 0.08, 0.09, 0.12);
            wrappedText(x, y - 16, warnings.stream().map(this::itemText).collect(Collectors.joining(" ")), 92, 2, 8.5, 10);
        }

        void footer(int page) {
            text(48, 30, "NexusAI Business Validator", "F1", 8, 0.45, 0.47, 0.55);
            text(540, 30, "Page " + page + " of 2", "F1", 8, 0.45, 0.47, 0.55);
        }

        void badge(double x, double y, String label) {
            fill(x, y, 118, 22, 0.31, 0.28, 0.90);
            text(x + 12, y + 7, label, "F2", 9, 1, 1, 1);
        }

        void card(double x, double y, double w, double h) {
            fill(x, y, w, h, 0.985, 0.988, 1);
            stroke(x, y, w, h, 0.86, 0.88, 0.92);
        }

        void fill(double x, double y, double w, double h, double r, double g, double b) {
            body.append(String.format(Locale.US, "q %.3f %.3f %.3f rg %.1f %.1f %.1f %.1f re f Q\n", r, g, b, x, y, w, h));
        }

        void stroke(double x, double y, double w, double h, double r, double g, double b) {
            body.append(String.format(Locale.US, "q %.3f %.3f %.3f RG 0.7 w %.1f %.1f %.1f %.1f re S Q\n", r, g, b, x, y, w, h));
        }

        void text(double x, double y, String value, String font, double size, double r, double g, double b) {
            body.append(String.format(Locale.US, "BT %.3f %.3f %.3f rg /%s %.1f Tf %.1f %.1f Td (%s) Tj ET\n",
                    r, g, b, font, size, x, y, escapePdf(cleanText(value))));
        }

        double wrappedText(double x, double y, String value, int maxChars, int maxLines, double size, double lineHeight) {
            List<String> lines = wrap(cleanText(value), maxChars);
            double current = y;
            int count = 0;
            for (String line : lines) {
                if (count++ >= maxLines) break;
                text(x, current, line, "F1", size, 0.12, 0.13, 0.16);
                current -= lineHeight;
            }
            return current;
        }

        private List<String> wrap(String text, int maxChars) {
            if (text == null || text.isBlank()) return List.of("Insufficient data available.");
            List<String> lines = new ArrayList<>();
            StringBuilder line = new StringBuilder();
            for (String word : text.split("\\s+")) {
                if (line.length() + word.length() + 1 > maxChars) {
                    lines.add(line.toString());
                    line = new StringBuilder(word);
                } else {
                    if (!line.isEmpty()) line.append(" ");
                    line.append(word);
                }
            }
            if (!line.isEmpty()) lines.add(line.toString());
            return lines;
        }

        private String itemText(Object value) {
            if (value instanceof Map<?, ?> item) {
                String name = firstMapText(item, "name", "full_name", "fullName");
                String reason = firstMapText(item, "reason");
                return (name + (reason.isBlank() ? "" : ": " + reason)).trim();
            }
            return String.valueOf(value);
        }

        private String fieldText(Map<String, Object> source, String key, String defaultValue) {
            Object value = source.get(key);
            if (value == null) {
                return defaultValue == null ? "" : defaultValue;
            }
            String text = cleanText(String.valueOf(value));
            return text.isBlank() ? (defaultValue == null ? "" : defaultValue) : text;
        }

        private String firstMapText(Map<?, ?> source, String... keys) {
            for (String key : keys) {
                if (source.containsKey(key) && source.get(key) != null) {
                    String text = cleanText(String.valueOf(source.get(key)));
                    if (!text.isBlank()) {
                        return text;
                    }
                }
            }
            return "";
        }

        @SuppressWarnings("unchecked")
        private Map<String, Object> map(Object value) {
            return value instanceof Map<?, ?> ? (Map<String, Object>) value : new LinkedHashMap<>();
        }
    }

    private String escapePdf(String value) {
        return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }
}
