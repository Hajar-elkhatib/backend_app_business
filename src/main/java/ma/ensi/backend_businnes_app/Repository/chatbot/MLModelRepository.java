package ma.ensi.backend_businnes_app.Repository.chatbot;

import ma.ensi.backend_businnes_app.Model.ai.MLModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MLModelRepository extends MongoRepository<MLModel, String> {

    Optional<MLModel> findByModelName(String modelName);
    List<MLModel> findByModelType(String modelType);
    Optional<MLModel> findByIsFallbackTrue();

    List<MLModel> findByAccuracyGreaterThanEqual(double minAccuracy);
    List<MLModel> findByF1ScoreGreaterThanEqual(double minF1Score);

    boolean existsByModelName(String modelName);
}