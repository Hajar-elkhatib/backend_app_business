package ma.ensi.backend_businnes_app.Repository.chatbot;

import ma.ensi.backend_businnes_app.Model.ai.EngineeredFeatures;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EngineeredFeaturesRepository extends MongoRepository<EngineeredFeatures, String> {

    Optional<EngineeredFeatures> findByProjectId(String projectId);
    boolean existsByProjectId(String projectId);
    void deleteByProjectId(String projectId);

    // Metric filters
    Optional<EngineeredFeatures> findFirstByProjectIdOrderByCreatedAtDesc(String projectId);
}