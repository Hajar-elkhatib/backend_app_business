package ma.ensi.backend_businnes_app.Repository.social;

import ma.ensi.backend_businnes_app.Model.social.Conversation_Mesage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConversationMessageRepository extends MongoRepository<Conversation_Mesage, String> {
    List<Conversation_Mesage> findByConversationId(String conversationId);
    List<Conversation_Mesage> findByConversationIdOrderByTimestampAsc(String conversationId);
    List<Conversation_Mesage> findByConversationIdAndRole(String conversationId, String role);
    long countByConversationId(String conversationId);
    void deleteByConversationId(String conversationId);
}