package ma.ensi.backend_businnes_app.Repository.chatbot;

import ma.ensi.backend_businnes_app.Model.ai.AIRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIRequestRepository extends MongoRepository<AIRequest, String> {

    List<AIRequest> findByChatId(String chatId);
    List<AIRequest> findByRequestType(String requestType);
    List<AIRequest> findByStatus(String status);
    List<AIRequest> findByEndpoint(String endpoint);

    List<AIRequest> findByChatIdAndRequestType(String chatId, String requestType);
    List<AIRequest> findByChatIdOrderByCreatedAtDesc(String chatId);

    long countByChatId(String chatId);
    long countByStatus(String status);
    void deleteByChatId(String chatId);
}