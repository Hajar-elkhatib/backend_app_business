package ma.ensi.backend_businnes_app.Repository.analysis;

import ma.ensi.backend_businnes_app.Model.analysis.SentimentAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SentimentAnalysisRepository extends MongoRepository<SentimentAnalysis, String> {

    List<SentimentAnalysis> findByProjectId(String projectId);
    Optional<SentimentAnalysis> findFirstByProjectIdOrderByCreatedAtDesc(String projectId);

    List<SentimentAnalysis> findByProjectIdAndTextSource(String projectId, String textSource);
    List<SentimentAnalysis> findBySentimentLabel(String sentimentLabel);
    List<SentimentAnalysis> findByModelVersion(String modelVersion);

    List<SentimentAnalysis> findByAverageSentimentScoreGreaterThanEqual(double minScore);
    List<SentimentAnalysis> findByConfidenceScoreGreaterThanEqual(double minConfidence);

    long countByProjectId(String projectId);
    void deleteByProjectId(String projectId);
}