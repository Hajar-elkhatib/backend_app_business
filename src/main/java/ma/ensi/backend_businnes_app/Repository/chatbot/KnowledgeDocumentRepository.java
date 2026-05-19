package ma.ensi.backend_businnes_app.Repository.chatbot;

import ma.ensi.backend_businnes_app.Model.ai.KnowledgeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeDocumentRepository extends MongoRepository<KnowledgeDocument, String> {

    List<KnowledgeDocument> findBySector(String sector);
    List<KnowledgeDocument> findBySourceType(String sourceType);
    List<KnowledgeDocument> findByTitleContaining(String keyword);
    List<KnowledgeDocument> findBySectorAndSourceType(String sector, String sourceType);

    boolean existsBySource(String source);
}