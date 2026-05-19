package ma.ensi.backend_businnes_app.Repository.chatbot;

import ma.ensi.backend_businnes_app.Model.ai.AIResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AIResponseRepository extends MongoRepository<AIResponse, String> {

    List<AIResponse> findByRequestId(String requestId);
    Optional<AIResponse> findFirstByRequestIdOrderByCreatedAtDesc(String requestId);

    List<AIResponse> findByModelName(String modelName);
    List<AIResponse> findByModelId(String modelId);
    List<AIResponse> findByFeedbackMode(String feedbackMode);
    List<AIResponse> findByLabel(String label);

    List<AIResponse> findByConfidenceScoreGreaterThanEqual(double minConfidence);

    boolean existsByRequestId(String requestId);
    long countByRequestId(String requestId);
    void deleteByRequestId(String requestId);
}