package ma.ensi.backend_businnes_app.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ma.ensi.backend_businnes_app.DTOS.response.StartupSuccessResponse;
import ma.ensi.backend_businnes_app.DTOS.request.BusinessValidationRequest;
import ma.ensi.backend_businnes_app.Model.analysis.BusinessIdeaAnalysis;
import ma.ensi.backend_businnes_app.Model.analysis.MarketAnalysis;
import ma.ensi.backend_businnes_app.Model.analysis.MarketFeedback;
import ma.ensi.backend_businnes_app.Model.analysis.SentimentAnalysis;
import ma.ensi.backend_businnes_app.Model.auth.Specialist;
import ma.ensi.backend_businnes_app.Model.core.Project;
import ma.ensi.backend_businnes_app.Model.social.SpecialistRecommendation;
import ma.ensi.backend_businnes_app.Repository.analysis.BusinessIdeaAnalysisRepository;
import ma.ensi.backend_businnes_app.Repository.analysis.MarketAnalysisRepository;
import ma.ensi.backend_businnes_app.Repository.analysis.MarketFeedbackRepository;
import ma.ensi.backend_businnes_app.Repository.analysis.SentimentAnalysisRepository;
import ma.ensi.backend_businnes_app.Repository.core.ProjectRepository;
import ma.ensi.backend_businnes_app.Repository.social.SpecialistRecommendationRepository;
import ma.ensi.backend_businnes_app.Repository.user.SpecialistRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String fastApiBaseUrl;
    private final ProjectRepository projectRepository;
    private final BusinessIdeaAnalysisRepository businessIdeaAnalysisRepository;
    private final MarketAnalysisRepository marketAnalysisRepository;
    private final MarketFeedbackRepository marketFeedbackRepository;
    private final SentimentAnalysisRepository sentimentAnalysisRepository;
    private final SpecialistRecommendationRepository specialistRecommendationRepository;
    private final SpecialistRepository specialistRepository;

    public AnalysisService(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${fastapi.base-url}") String fastApiBaseUrl,
            ProjectRepository projectRepository,
            BusinessIdeaAnalysisRepository businessIdeaAnalysisRepository,
            MarketAnalysisRepository marketAnalysisRepository,
            MarketFeedbackRepository marketFeedbackRepository,
            SentimentAnalysisRepository sentimentAnalysisRepository,
            SpecialistRecommendationRepository specialistRecommendationRepository,
            SpecialistRepository specialistRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.fastApiBaseUrl = trimTrailingSlash(fastApiBaseUrl);
        this.projectRepository = projectRepository;
        this.businessIdeaAnalysisRepository = businessIdeaAnalysisRepository;
        this.marketAnalysisRepository = marketAnalysisRepository;
        this.marketFeedbackRepository = marketFeedbackRepository;
        this.sentimentAnalysisRepository = sentimentAnalysisRepository;
        this.specialistRecommendationRepository = specialistRecommendationRepository;
        this.specialistRepository = specialistRepository;
    }

    public BusinessIdeaAnalysis analyzeProject(String projectId) {
        return analyzeProject(projectId, null);
    }

    public BusinessIdeaAnalysis analyzeProject(String projectId, BusinessValidationRequest request) {
        Project project = findProject(projectId);

        if (request != null && request.getOpinions() != null && !request.getOpinions().isBlank()) {
            createFeedbacks(projectId, splitOpinions(request.getOpinions()));
        }

        Map<String, Object> payload = buildBusinessValidationPayload(project);
        Map<String, Object> response = postForMap("/api/v1/business-validation/analyze", payload);

        BusinessIdeaAnalysis analysis = new BusinessIdeaAnalysis();
        analysis.setProjectId(projectId);
        Map<String, Object> scores = objectMap(response.get("scores"));
        Map<String, Object> startupPrediction = objectMap(response.get("startupPrediction"));
        analysis.setPredictionScore(number(scores, "startupSuccessScore", number(startupPrediction, "success_probability", 0.0)));
        analysis.setPredictionLabel(text(startupPrediction, "prediction_label", scoreLabel(number(scores, "finalScore", 0.0))));
        analysis.setSuccessProbability(number(startupPrediction, "success_probability", analysis.getPredictionScore()));
        analysis.setModelMode(text(startupPrediction, "model_mode", ""));
        analysis.setConfidenceScore(number(response, "confidence_score", 0.0));
        analysis.setFinalScore(number(scores, "finalScore", 0.0));
        analysis.setStartupSuccessScore(number(scores, "startupSuccessScore", analysis.getPredictionScore()));
        analysis.setMarketAnalysisScore(number(scores, "marketAnalysisScore", 0.0));
        analysis.setMarketOpinionScore(number(scores, "marketOpinionScore", 0.0));

        analysis.setTractionPerEmployee(project.getProductTractionUsers() / Math.max(project.getTeamSize(), 1));
        analysis.setRevenuePerUser(project.getRevenueMillion() / Math.max(project.getProductTractionUsers(), 1.0));
        analysis.setBurnToRevenueRatio(project.getBurnRateMillion() / Math.max(project.getRevenueMillion(), 0.000001));
        analysis.setStrengths(stringList(response.get("strengths")));
        analysis.setWeaknesses(stringList(response.get("weaknesses")));
        analysis.setWarnings(stringList(response.get("warnings")));
        analysis.setRecommendations(stringList(response.get("recommendations")));
        analysis.setGeneratedNeeds(stringList(response.get("generatedNeeds")));
        analysis.setInterpretation(text(response, "interpretation", ""));
        analysis.setInterpretationSource(text(response, "interpretationSource", ""));
        analysis.setShapExplanation(objectMap(response.get("shapExplanation")));
        analysis.setRawAnalysis(response);
        analysis.setModelVersion("business-validation-analyze-v1");
        analysis.setCreatedAt(new Date());

        project.setProjectStatus("ANALYZING");
        projectRepository.save(project);
        BusinessIdeaAnalysis saved = businessIdeaAnalysisRepository.save(analysis);
        saveRecommendedSpecialists(projectId, response);
        updateFeedbackScores(projectId, response);
        return saved;
    }

    public StartupSuccessResponse predictStartupSuccess(String projectId) {
        Project project = findProject(projectId);

        Map<String, Object> response = postForMap(
                "/api/v1/startup-success/predict",
                buildStartupSuccessPayload(project)
        );

        StartupSuccessResponse result = new StartupSuccessResponse();
        result.setProjectId(projectId);
        result.setSuccessProbability(number(response, "success_probability", 0.0));
        result.setPredictionLabel(text(response, "prediction_label", "UNKNOWN"));
        result.setConfidenceScore(100.0);
        result.setModelVersion(text(response, "model_mode", "unknown"));
        return result;
    }

    public SentimentAnalysis analyzeSentiment(String projectId, String text, String textSource) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("texts", List.of(text));

        Map<String, Object> response = postForMap("/api/v1/sentiment/analyze", payload);
        List<Map<String, Object>> results = objectList(response.get("results"));
        Map<String, Object> firstResult = results.isEmpty() ? Map.of() : results.get(0);

        SentimentAnalysis analysis = new SentimentAnalysis();
        analysis.setProjectId(projectId);
        analysis.setTextSource(textSource);
        analysis.setInewText(text);
        analysis.setSentimentLabel(text(firstResult, "sentiment_label", text(response, "overall_label", "unknown")));
        analysis.setSentimentScore(number(firstResult, "sentiment_score", 0.0));
        analysis.setAverageSentimentScore(number(response, "average_sentiment_score", 0.0));
        analysis.setConfidenceScore(100.0);
        analysis.setCount((int) number(response, "count", results.size()));
        analysis.setModelVersion(text(response, "model_mode", "unknown"));
        analysis.setCreatedAt(new Date());

        return sentimentAnalysisRepository.save(analysis);
    }

    public MarketAnalysis analyzeMarket(String projectId) {
        Project project = findProject(projectId);

        Map<String, Object> response = postForMap(
                "/api/v1/market-analysis/score",
                buildMarketAnalysisPayload(project)
        );

        Map<String, Object> market = objectMap(response.get("market_analysis"));
        Map<String, Object> features = objectMap(response.get("features"));
        Map<String, Object> subScores = objectMap(market.get("sub_scores"));

        MarketAnalysis analysis = new MarketAnalysis();
        analysis.setProjectId(projectId);
        analysis.setSector(text(market, "normalized_sector", project.getSector()));
        analysis.setRegion(project.getRegion());
        analysis.setCountryCode(text(features, "country_code", project.getCountryCode()));
        analysis.setMarketSize(number(features, "market_size_billion", project.getMarketSizeBillion()));
        analysis.setGrowthRate(number(features, "market_growth_rate_percent", project.getMarketGrowthRatePercent()));
        analysis.setCompetitionLevel(project.getCompetitionLevel());
        analysis.setPriority(text(market, "market_label", "UNKNOWN"));
        analysis.setProductTractionUsers((int) number(features, "product_traction_users", project.getProductTractionUsers()));
        analysis.setCompetitionCount((int) number(features, "competition_count", 0.0));
        analysis.setGeographicFitScore(number(subScores, "geographic_fit", 0.0));
        analysis.setTrendScore(number(subScores, "trend", 0.0));
        analysis.setConfidenceScore(number(market, "confidence_score", 0.0));
        analysis.setKeywords(text(features, "keyword", ""));
        analysis.setDataSource("fastapi-market-analysis");
        analysis.setModelVersion("market-analysis-api-v0.2.0");
        analysis.setCreatedAt(new Date());

        return marketAnalysisRepository.save(analysis);
    }

    public List<SpecialistRecommendation> recommendSpecialists(String projectId) {
        Project project = findProject(projectId);
        List<Specialist> specialists = specialistRepository.findByAvailabilityStatus("AVAILABLE");

        Map<String, Object> response = postForMap(
                "/api/v1/specialists/recommend",
                buildSpecialistRecommendationPayload(project, specialists)
        );

        specialistRecommendationRepository.deleteByProjectId(projectId);

        List<Map<String, Object>> recommendations = objectList(response.get("recommendations"));
        List<SpecialistRecommendation> savedRecommendations = new ArrayList<>();
        for (int i = 0; i < recommendations.size(); i++) {
            Map<String, Object> item = recommendations.get(i);
            SpecialistRecommendation recommendation = new SpecialistRecommendation();
            recommendation.setProjectId(projectId);
            recommendation.setSpecialistId(text(item, "specialist_id", ""));
            recommendation.setSpecialistName(text(item, "full_name", ""));
            recommendation.setExpertiseDomain(text(item, "expertise_domain", ""));
            recommendation.setRecommendedScore(number(item, "recommended_score", 0.0));
            recommendation.setRank(i + 1);
            recommendation.setScoreDetails(toJson(item.get("score_details")));
            recommendation.setReason(text(item, "reason", ""));
            recommendation.setCreatedAt(new Date());
            savedRecommendations.add(specialistRecommendationRepository.save(recommendation));
        }

        return savedRecommendations;
    }

    public BusinessIdeaAnalysis getLatestAnalysis(String projectId) {
        return businessIdeaAnalysisRepository.findFirstByProjectIdOrderByCreatedAtDesc(projectId)
                .orElseThrow(() -> new RuntimeException("No analysis found"));
    }

    public List<BusinessIdeaAnalysis> getAnalysisHistory(String projectId) {
        return businessIdeaAnalysisRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    public List<MarketFeedback> createFeedbacks(String projectId, List<String> texts) {
        List<MarketFeedback> saved = new ArrayList<>();
        for (String text : texts) {
            if (text == null || text.isBlank()) continue;
            MarketFeedback feedback = new MarketFeedback();
            feedback.setProjectId(projectId);
            feedback.setFeedbackText(text.trim());
            feedback.setCreatedAt(LocalDateTime.now());
            saved.add(marketFeedbackRepository.save(feedback));
        }
        return saved;
    }

    public List<MarketFeedback> getFeedbacks(String projectId) {
        return marketFeedbackRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    public List<MarketFeedback> analyzeFeedbacks(String projectId) {
        List<MarketFeedback> feedbacks = marketFeedbackRepository.findByProjectIdOrderByCreatedAtAsc(projectId);
        List<String> texts = feedbacks.stream()
                .map(MarketFeedback::getFeedbackText)
                .filter(text -> text != null && !text.isBlank())
                .collect(Collectors.toList());
        if (texts.isEmpty()) {
            return feedbacks;
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("texts", texts);
        Map<String, Object> response = postForMap("/api/v1/sentiment/analyze", payload);
        List<Map<String, Object>> results = objectList(response.get("results"));
        for (int i = 0; i < feedbacks.size() && i < results.size(); i++) {
            Map<String, Object> item = results.get(i);
            MarketFeedback feedback = feedbacks.get(i);
            feedback.setSentimentLabel(text(item, "sentiment_label", ""));
            feedback.setSentimentScore(number(item, "sentiment_score", 0.0));
            marketFeedbackRepository.save(feedback);
        }
        return marketFeedbackRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
    }

    private Map<String, Object> buildBusinessValidationPayload(Project project) {
        Map<String, Object> payload = buildStartupSuccessPayload(project);
        payload.put("country", defaultText(project.getCountry(), "Morocco"));
        payload.put("region", project.getRegion());
        payload.put("country_code", project.getCountryCode());
        payload.put("market_growth_rate_percent", project.getMarketGrowthRatePercent());
        payload.put("competition_level", project.getCompetitionLevel());
        payload.put("search_trend_score", project.getSearchTrendScore());
        payload.put("use_world_bank", project.isUserWordBank());
        payload.put("opinions", feedbackTexts(project.getId(), project.getOpinions()));
        return payload;
    }

    private Map<String, Object> buildStartupSuccessPayload(Project project) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("project_name", defaultText(project.getTitle(), "Untitled project"));
        payload.put("project_description", defaultText(project.getDescription(), ""));
        payload.put("sector", defaultText(project.getSector(), "Other"));
        payload.put("founder_experience_years", project.getFounderExperienceYears());
        payload.put("funding_rounds", Math.max(project.getFundingRounds(), 1));
        payload.put("team_size", Math.max(project.getTeamSize(), 1));
        payload.put("market_size_billion", Math.max(project.getMarketSizeBillion(), 0.0));
        payload.put("product_traction_users", (int) Math.max(project.getProductTractionUsers(), 0.0));
        payload.put("burn_rate_million", Math.max(project.getBurnRateMillion(), 0.0));
        payload.put("revenue_million", Math.max(project.getRevenueMillion(), 0.000001));
        payload.put("investor_type", defaultText(project.getInvestorType(), "none"));
        payload.put("founder_background", "first_time");
        return payload;
    }

    private Map<String, Object> buildMarketAnalysisPayload(Project project) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("project_name", defaultText(project.getTitle(), "Untitled project"));
        payload.put("sector", defaultText(project.getSector(), "Other"));
        payload.put("country", defaultText(project.getCountry(), "Morocco"));
        payload.put("country_code", project.getCountryCode());
        payload.put("keyword", project.getSector());
        payload.put("market_size_billion", Math.max(project.getMarketSizeBillion(), 0.0));
        payload.put("market_growth_rate_percent", project.getMarketGrowthRatePercent());
        payload.put("competition_level", project.getCompetitionLevel());
        payload.put("product_traction_users", (int) Math.max(project.getProductTractionUsers(), 0.0));
        payload.put("search_trend_score", project.getSearchTrendScore());
        payload.put("use_world_bank", project.isUserWordBank());
        return payload;
    }

    private Map<String, Object> buildSpecialistRecommendationPayload(Project project, List<Specialist> specialists) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("project_id", project.getId());
        payload.put("title", defaultText(project.getTitle(), "Untitled project"));
        payload.put("description", defaultText(project.getDescription(), ""));
        payload.put("sector", defaultText(project.getSector(), "Other"));
        payload.put("needs", splitOpinions(project.getOpinions()));
        payload.put("project_stage", project.getProjectStatus());
        payload.put("location", project.getRegion());
        payload.put("top_k", 5);
        payload.put("specialists", specialists.stream()
                .map(this::buildSpecialistPayload)
                .collect(Collectors.toList()));
        return payload;
    }

    private Map<String, Object> buildSpecialistPayload(Specialist specialist) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("specialist_id", specialist.getId());
        payload.put("full_name", defaultText(specialist.getFullName(), ""));
        payload.put("profession", specialist.getProfession());
        payload.put("expertise_domain", specialist.getExpertiseDomain());
        payload.put("skills", specialist.getSkills());
        payload.put("sectors", specialist.getSectors());
        payload.put("industry_experience", specialist.getIndustryExperience());
        payload.put("hourly_rate", specialist.getHourlyRate());
        payload.put("languages", specialist.getLanguages());
        payload.put("location", specialist.getLocation());
        payload.put("average_rating", specialist.getRating());
        payload.put("reviews_count", (int) specialist.getReviewsCount());
        payload.put("availability_status", specialist.getAvailabilityStatus());
        payload.put("bio", specialist.getBio());
        payload.put("completed_projects", specialist.getCompletedProjects());
        return payload;
    }

    private Project findProject(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> postForMap(String endpoint, Map<String, Object> payload) {
        Map<String, Object> response = restTemplate.postForObject(
                fastApiBaseUrl + endpoint,
                payload,
                Map.class
        );
        return response == null ? Map.of() : response;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> objectMap(Object value) {
        return value instanceof Map<?, ?> ? (Map<String, Object>) value : Map.of();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> objectList(Object value) {
        return value instanceof List<?> ? (List<Map<String, Object>>) value : List.of();
    }

    private double number(Map<String, Object> source, String key, double defaultValue) {
        Object value = source.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String text) {
            try {
                return Double.parseDouble(text);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private String text(Map<String, Object> source, String key, String defaultValue) {
        Object value = source.get(key);
        return value == null ? defaultValue : String.valueOf(value);
    }

    private String defaultText(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private List<String> splitOpinions(String opinions) {
        if (opinions == null || opinions.isBlank()) {
            return List.of();
        }
        return Arrays.stream(opinions.split("\\r?\\n|;"))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .collect(Collectors.toList());
    }

    private List<String> feedbackTexts(String projectId, String projectOpinions) {
        List<String> texts = new ArrayList<>(splitOpinions(projectOpinions));
        texts.addAll(marketFeedbackRepository.findByProjectIdOrderByCreatedAtAsc(projectId).stream()
                .map(MarketFeedback::getFeedbackText)
                .filter(text -> text != null && !text.isBlank())
                .toList());
        return texts.stream().distinct().collect(Collectors.toList());
    }

    private void saveRecommendedSpecialists(String projectId, Map<String, Object> response) {
        List<Map<String, Object>> recommendations = objectList(response.get("recommendedSpecialists"));
        if (recommendations.isEmpty()) return;
        specialistRecommendationRepository.deleteByProjectId(projectId);
        String needs = toJson(response.get("generatedNeeds"));
        for (int i = 0; i < recommendations.size(); i++) {
            Map<String, Object> item = recommendations.get(i);
            SpecialistRecommendation recommendation = new SpecialistRecommendation();
            recommendation.setProjectId(projectId);
            recommendation.setSpecialistId(text(item, "specialist_id", text(item, "specialistId", "")));
            recommendation.setSpecialistName(text(item, "full_name", text(item, "specialistName", "")));
            recommendation.setExpertiseDomain(text(item, "expertise_domain", text(item, "expertiseDomain", "")));
            recommendation.setRecommendedScore(number(item, "recommended_score", number(item, "recommendedScore", 0.0)));
            recommendation.setRank(i + 1);
            recommendation.setReason(text(item, "reason", ""));
            recommendation.setScoreDetails(toJson(item.get("score_details")));
            recommendation.setGeneratedNeeds(needs);
            recommendation.setCreatedAt(new Date());
            specialistRecommendationRepository.save(recommendation);
        }
    }

    private void updateFeedbackScores(String projectId, Map<String, Object> response) {
        Map<String, Object> sentiment = objectMap(response.get("sentimentAnalysis"));
        List<Map<String, Object>> results = objectList(sentiment.get("results"));
        if (results.isEmpty()) return;
        List<MarketFeedback> feedbacks = marketFeedbackRepository.findByProjectIdOrderByCreatedAtAsc(projectId);
        for (int i = 0; i < feedbacks.size() && i < results.size(); i++) {
            MarketFeedback feedback = feedbacks.get(i);
            Map<String, Object> result = results.get(i);
            feedback.setSentimentLabel(text(result, "sentiment_label", ""));
            feedback.setSentimentScore(number(result, "sentiment_score", 0.0));
            marketFeedbackRepository.save(feedback);
        }
    }

    private List<String> stringList(Object value) {
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).collect(Collectors.toList());
        }
        if (value instanceof String text && !text.isBlank()) {
            return List.of(text);
        }
        return List.of();
    }

    private String scoreLabel(double score) {
        if (score >= 70) return "Strong validation";
        if (score >= 50) return "Moderate validation";
        return "Weak validation";
    }

    private String mergeOpinions(String existing, String incoming) {
        if (incoming == null || incoming.isBlank()) {
            return existing;
        }
        if (existing == null || existing.isBlank()) {
            return incoming.trim();
        }
        if (existing.contains(incoming.trim())) {
            return existing;
        }
        return existing.trim() + System.lineSeparator() + incoming.trim();
    }

    private String toJson(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof String text) {
            return text;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            return String.valueOf(value);
        }
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
