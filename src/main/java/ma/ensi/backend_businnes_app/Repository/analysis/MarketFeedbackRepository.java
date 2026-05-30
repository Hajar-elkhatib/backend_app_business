package ma.ensi.backend_businnes_app.Repository.analysis;

import ma.ensi.backend_businnes_app.Model.analysis.MarketFeedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketFeedbackRepository extends MongoRepository<MarketFeedback, String> {
    List<MarketFeedback> findByProjectIdOrderByCreatedAtDesc(String projectId);
    List<MarketFeedback> findByProjectIdOrderByCreatedAtAsc(String projectId);
    void deleteByProjectId(String projectId);
}
