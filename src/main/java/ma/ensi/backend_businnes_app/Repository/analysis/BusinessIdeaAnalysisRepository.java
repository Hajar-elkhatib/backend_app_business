package ma.ensi.backend_businnes_app.Repository.analysis;

import ma.ensi.backend_businnes_app.Model.analysis.BusinessIdeaAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessIdeaAnalysisRepository extends MongoRepository<BusinessIdeaAnalysis, String> {

    Optional<BusinessIdeaAnalysis> findByProjectId(String projectId);
    Optional<BusinessIdeaAnalysis> findFirstByProjectIdOrderByCreatedAtDesc(String projectId);
    List<BusinessIdeaAnalysis> findByProjectIdOrderByCreatedAtDesc(String projectId);
    boolean existsByProjectId(String projectId);
    void deleteByProjectId(String projectId);

    List<BusinessIdeaAnalysis> findByPredictionLabel(String predictionLabel);
    List<BusinessIdeaAnalysis> findByModelVersion(String modelVersion);
    List<BusinessIdeaAnalysis> findByFinalScoreGreaterThanEqual(double minScore);
    List<BusinessIdeaAnalysis> findByConfidenceScoreGreaterThanEqual(double minConfidence);
}
