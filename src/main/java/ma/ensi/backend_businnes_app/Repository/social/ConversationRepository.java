package ma.ensi.backend_businnes_app.Repository.social;

import ma.ensi.backend_businnes_app.Model.social.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    List<Conversation> findByEntrepreneurId(String entrepreneurId);
    List<Conversation> findBySpecialistId(String specialistId);
    List<Conversation> findByProjectId(String projectId);
    Optional<Conversation> findByEntrepreneurIdAndSpecialistId(String entrepreneurId, String specialistId);
    boolean existsByEntrepreneurIdAndSpecialistId(String entrepreneurId, String specialistId);
    void deleteByEntrepreneurId(String entrepreneurId);
    void deleteBySpecialistId(String specialistId);
}