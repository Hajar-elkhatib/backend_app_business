package ma.ensi.backend_businnes_app.Repository.chatbot;

import ma.ensi.backend_businnes_app.Model.ai.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends MongoRepository<Recommendation, String> {

    List<Recommendation> findByProjectId(String projectId);
    List<Recommendation> findByAnalysisId(String analysisId);
    List<Recommendation> findByProjectIdAndAnalysisId(String projectId, String analysisId);

    List<Recommendation> findByRecommendationType(String recommendationType);
    List<Recommendation> findByProjectIdAndRecommendationType(String projectId, String recommendationType);
    List<Recommendation> findByConfidenceScoreGreaterThanEqual(double minConfidence);

    long countByProjectId(String projectId);
    void deleteByProjectId(String projectId);
    void deleteByAnalysisId(String analysisId);
}