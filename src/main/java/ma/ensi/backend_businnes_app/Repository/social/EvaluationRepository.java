package ma.ensi.backend_businnes_app.Repository.social;

import ma.ensi.backend_businnes_app.Model.social.Evaluation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvaluationRepository extends MongoRepository<Evaluation, String> {
    List<Evaluation> findBySpecialistId(String specialistId);
    List<Evaluation> findByEntrepreneurId(String entrepreneurId);
    List<Evaluation> findBySpecialistIdAndStatus(String specialistId, String status);
    List<Evaluation> findByEntrepreneurIdAndSpecialistId(String entrepreneurId, String specialistId);
    long countBySpecialistId(String specialistId);
}