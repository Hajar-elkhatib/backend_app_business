package ma.ensi.backend_businnes_app.Service;

import ma.ensi.backend_businnes_app.Model.analysis.*;

import ma.ensi.backend_businnes_app.Model.core.Project;
import ma.ensi.backend_businnes_app.Model.social.SpecialistRecommendation;
import ma.ensi.backend_businnes_app.Model.auth.Specialist;
import ma.ensi.backend_businnes_app.Repository.analysis.*;

import ma.ensi.backend_businnes_app.Repository.core.ProjectRepository;
import ma.ensi.backend_businnes_app.Repository.social.SpecialistRecommendationRepository;
import ma.ensi.backend_businnes_app.Repository.user.SpecialistRepository;
import ma.ensi.backend_businnes_app.DTOS.request.*;
import ma.ensi.backend_businnes_app.DTOS.response.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final RestTemplate restTemplate;
    private final String fastApiBaseUrl;
    private final ProjectRepository projectRepository;
    private final BusinessIdeaAnalysisRepository businessIdeaAnalysisRepository;
    private final MarketAnalysisRepository marketAnalysisRepository;
    private final SentimentAnalysisRepository sentimentAnalysisRepository;
    private final SpecialistRecommendationRepository specialistRecommendationRepository;
    private final SpecialistRepository specialistRepository;

    // ✅ No AIRequest or AIResponse here anymore
    public AnalysisService(
            RestTemplate restTemplate,
            @Value("${fastapi.base-url}") String fastApiBaseUrl,
            ProjectRepository projectRepository,
            BusinessIdeaAnalysisRepository businessIdeaAnalysisRepository,
            MarketAnalysisRepository marketAnalysisRepository,
            SentimentAnalysisRepository sentimentAnalysisRepository,
            SpecialistRecommendationRepository specialistRecommendationRepository,
            SpecialistRepository specialistRepository) {
        this.restTemplate = restTemplate;
        this.fastApiBaseUrl = fastApiBaseUrl;
        this.projectRepository = projectRepository;
        this.businessIdeaAnalysisRepository = businessIdeaAnalysisRepository;
        this.marketAnalysisRepository = marketAnalysisRepository;
        this.sentimentAnalysisRepository = sentimentAnalysisRepository;
        this.specialistRecommendationRepository = specialistRecommendationRepository;
        this.specialistRepository = specialistRepository;
    }

    // ✅ 1. Business Validation — clean version
    public BusinessIdeaAnalysis analyzeProject(String projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        BusinessValidationRequest request = new BusinessValidationRequest();
        request.setProjectId(projectId);
        request.setSector(project.getSector());
        request.setCountry(project.getCountry());
        request.setFounderExperienceYears(project.getFounderExperienceYears());
        request.setFundingRounds(project.getFundingRounds());
        request.setTeamSize(project.getTeamSize());
        request.setMarketSizeBillion(project.getMarketSizeBillion());
        request.setMarketGrowthRatePercent(project.getMarketGrowthRatePercent());
        request.setProductTractionUsers(project.getProductTractionUsers());
        request.setBurnRateMillion(project.getBurnRateMillion());
        request.setRevenueMillion(project.getRevenueMillion());
        request.setInvestorType(project.getInvestorType());
        request.setCompetitionLevel(project.getCompetitionLevel());
        request.setSearchTrendScore(project.getSearchTrendScore());
        request.setUserWordBank(project.isUserWordBank());
        request.setOpinions(project.getOpinions());

        // Call FastAPI
        BusinessValidationResponse response = restTemplate.postForObject(
                fastApiBaseUrl + "/api/v1/business-validation/score",
                request,
                BusinessValidationResponse.class
        );

        // Save result directly
        BusinessIdeaAnalysis analysis = new BusinessIdeaAnalysis();
        analysis.setProjectId(projectId);
        analysis.setPredictionScore(response.getPredictionScore());
        analysis.setPredictionLabel(response.getPredictionLabel());
        analysis.setConfidenceScore(response.getConfidenceScore());
        analysis.setFinalScore(response.getFinalScore());
        analysis.setMarketAnalysisScore(response.getMarketAnalysisScore());
        analysis.setTractionPerEmployee(response.getTractionPerEmployee());
        analysis.setRevenuePerUser(response.getRevenuePerUser());
        analysis.setBurnToRevenueRatio(response.getBurnToRevenueRatio());
        analysis.setStrengths(response.getStrengths());
        analysis.setWeaknesses(response.getWeaknesses());
        analysis.setWarnings(response.getWarnings());
        analysis.setRecommendations(response.getRecommendations());
        analysis.setModelVersion(response.getModelVersion());
        analysis.setCreatedAt(new Date());

        project.setProjectStatus("ANALYZING");
        projectRepository.save(project);

        return businessIdeaAnalysisRepository.save(analysis);
    }

    // ✅ 2. Startup Success
    public StartupSuccessResponse predictStartupSuccess(String projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        StartupSuccessRequest request = new StartupSuccessRequest();
        request.setProjectId(projectId);
        request.setFounderExperienceYears(project.getFounderExperienceYears());
        request.setFundingRounds(project.getFundingRounds());
        request.setTeamSize(project.getTeamSize());
        request.setBurnRateMillion(project.getBurnRateMillion());
        request.setRevenueMillion(project.getRevenueMillion());
        request.setMarketSizeBillion(project.getMarketSizeBillion());
        request.setInvestorType(project.getInvestorType());

        // Call FastAPI directly
        return restTemplate.postForObject(
                fastApiBaseUrl + "/api/v1/startup-success/predict",
                request,
                StartupSuccessResponse.class
        );
    }

    // ✅ 3. Sentiment Analysis
    public SentimentAnalysis analyzeSentiment(String projectId,
                                              String text,
                                              String textSource) {
        SentimentRequest request = new SentimentRequest();
        request.setProjectId(projectId);
        request.setText(text);
        request.setTextSource(textSource);

        // Call FastAPI
        SentimentResponse response = restTemplate.postForObject(
                fastApiBaseUrl + "/api/v1/sentiment/analyze",
                request,
                SentimentResponse.class
        );

        // Save result directly
        SentimentAnalysis analysis = new SentimentAnalysis();
        analysis.setProjectId(projectId);
        analysis.setTextSource(textSource);
        analysis.setInewText(text);
        analysis.setSentimentLabel(response.getSentimentLabel());
        analysis.setSentimentScore(response.getSentimentScore());
        analysis.setAverageSentimentScore(response.getAverageSentimentScore());
        analysis.setConfidenceScore(response.getConfidenceScore());
        analysis.setModelVersion(response.getModelVersion());
        analysis.setCreatedAt(new Date());

        return sentimentAnalysisRepository.save(analysis);
    }

    // ✅ 4. Market Analysis
    public MarketAnalysis analyzeMarket(String projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        MarketAnalysisRequest request = new MarketAnalysisRequest();
        request.setProjectId(projectId);
        request.setSector(project.getSector());
        request.setRegion(project.getRegion());
        request.setCountryCode(project.getCountryCode());
        request.setMarketSize(project.getMarketSizeBillion());
        request.setGrowthRate(project.getMarketGrowthRatePercent());
        request.setProductTractionUsers((int) project.getProductTractionUsers());
        request.setCompetitionLevel(project.getCompetitionLevel());

        // Call FastAPI
        MarketAnalysisResponse response = restTemplate.postForObject(
                fastApiBaseUrl + "/api/v1/market-analysis/score",
                request,
                MarketAnalysisResponse.class
        );

        // Save result directly
        MarketAnalysis analysis = new MarketAnalysis();
        analysis.setProjectId(projectId);
        analysis.setSector(response.getSector());
        analysis.setRegion(response.getRegion());
        analysis.setMarketSize(response.getMarketSize());
        analysis.setGrowthRate(response.getGrowthRate());
        analysis.setCompetitionLevel(response.getCompetitionLevel());
        analysis.setPriority(response.getPriority());
        analysis.setGeographicFitScore(response.getGeographicFitScore());
        analysis.setTrendScore(response.getTrendScore());
        analysis.setConfidenceScore(response.getConfidenceScore());
        analysis.setModelVersion(response.getModelVersion());
        analysis.setCreatedAt(new Date());

        return marketAnalysisRepository.save(analysis);
    }

    // ✅ 5. Specialist Recommendation
    public List<SpecialistRecommendation> recommendSpecialists(String projectId) {

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<Specialist> specialists = specialistRepository
                .findByAvailabilityStatus("AVAILABLE");

        SpecialistRecommendationRequest request =
                new SpecialistRecommendationRequest();
        request.setProjectId(projectId);
        request.setSector(project.getSector());

        List<SpecialistRecommendationRequest.SpecialistData> specialistDataList =
                specialists.stream().map(s -> {
                    SpecialistRecommendationRequest.SpecialistData data =
                            new SpecialistRecommendationRequest.SpecialistData();
                    data.setSpecialistId(s.getId());
                    data.setProfession(s.getProfession());
                    data.setExpertiseDomain(s.getExpertiseDomain());
                    data.setSkills(s.getSkills());
                    data.setSectors(s.getSectors());
                    data.setRating(s.getRating());
                    data.setIndustryExperience(s.getIndustryExperience());
                    data.setHourlyRate(s.getHourlyRate());
                    data.setAvailabilityStatus(s.getAvailabilityStatus());
                    return data;
                }).collect(Collectors.toList());

        request.setSpecialists(specialistDataList);

        // Call FastAPI
        SpecialistRecommendationResponse response = restTemplate.postForObject(
                fastApiBaseUrl + "/api/v1/specialists/recommend",
                request,
                SpecialistRecommendationResponse.class
        );

        // Delete old recommendations
        specialistRecommendationRepository.deleteByProjectId(projectId);

        // Save new recommendations directly
        return response.getRecommendations()
                .stream().map(r -> {
                    SpecialistRecommendation recommendation =
                            new SpecialistRecommendation();
                    recommendation.setProjectId(projectId);
                    recommendation.setSpecialistId(r.getSpecialistId());
                    recommendation.setRecommendedScore(r.getRecommendedScore());
                    recommendation.setRank(r.getRank());
                    recommendation.setScoreDetails(r.getScoreDetails());
                    recommendation.setReason(r.getReason());
                    recommendation.setCreatedAt(new Date());
                    return specialistRecommendationRepository.save(recommendation);
                }).collect(Collectors.toList());
    }
}