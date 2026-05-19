package ma.ensi.backend_businnes_app.Model.ai;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "knowledge_documents")
public class KnowledgeDocument {

    @Id
    private String id;

    private String title;
    private String sector;         // filter documents by sector for better RAG relevance
    private String description;
    private String sourceType;
    private String source;         // URL or file path

    private List<Double> embeddingVector; // semantic embedding of the document content

    private Date createdAt;
}


