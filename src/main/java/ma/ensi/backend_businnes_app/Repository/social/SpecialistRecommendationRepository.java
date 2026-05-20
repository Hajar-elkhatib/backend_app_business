package ma.ensi.backend_businnes_app.Repository.social;

import ma.ensi.backend_businnes_app.Model.social.SpecialistRecommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialistRecommendationRepository extends MongoRepository<SpecialistRecommendation, String> {
    List<SpecialistRecommendation> findByProjectId(String projectId);
    List<SpecialistRecommendation> findBySpecialistId(String specialistId);
    List<SpecialistRecommendation> findByProjectIdOrderByRankAsc(String projectId);
    Optional<SpecialistRecommendation> findByProjectIdAndSpecialistId(String projectId, String specialistId);
    boolean existsByProjectIdAndSpecialistId(String projectId, String specialistId);
    void deleteByProjectId(String projectId);
}