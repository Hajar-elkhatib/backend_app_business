package ma.ensi.backend_businnes_app.Repository.core;

import ma.ensi.backend_businnes_app.Model.core.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    // By user
    List<Chat> findByUserId(String userId);
    List<Chat> findByUserIdOrderByCreatedAtDesc(String userId);
    List<Chat> findByUserIdOrderByUpdatedAtDesc(String userId);

    // By project
    List<Chat> findByProjectId(String projectId);
    Optional<Chat> findByIdAndUserId(String id, String userId);

    // By context type
    List<Chat> findByContextType(String contextType);
    List<Chat> findByUserIdAndContextType(String userId, String contextType);

    // By label
    List<Chat> findByChatLabel(String chatLabel);

    // Checks
    boolean existsByIdAndUserId(String id, String userId);
    long countByUserId(String userId);

    void deleteByUserId(String userId);
    void deleteByProjectId(String projectId);
}
