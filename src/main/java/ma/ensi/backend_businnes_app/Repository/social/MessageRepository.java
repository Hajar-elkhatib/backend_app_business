package ma.ensi.backend_businnes_app.Repository.social;

import ma.ensi.backend_businnes_app.Model.social.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByChatId(String chatId);
    List<Message> findByChatIdOrderByTimestampAsc(String chatId);
    List<Message> findByChatIdAndRole(String chatId, String role);
    Optional<Message> findFirstByChatIdOrderByTimestampDesc(String chatId);
    long countByChatId(String chatId);
    void deleteByChatId(String chatId);
}