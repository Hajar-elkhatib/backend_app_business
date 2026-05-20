package ma.ensi.backend_businnes_app.Repository.social;

import ma.ensi.backend_businnes_app.Model.social.Insight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InsightRepository extends MongoRepository<Insight, String> {
    List<Insight> findByProjectId(String projectId);
    List<Insight> findByProjectIdAndInsightType(String projectId, String insightType);
    List<Insight> findByProjectIdAndImportanceLevel(String projectId, String importanceLevel);
    void deleteByProjectId(String projectId);
}