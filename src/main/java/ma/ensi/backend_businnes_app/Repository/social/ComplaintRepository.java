package ma.ensi.backend_businnes_app.Repository.social;

import ma.ensi.backend_businnes_app.Model.social.Complaint;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends MongoRepository<Complaint, String> {
    List<Complaint> findByUserId(String userId);
    List<Complaint> findByStatus(String status);
    List<Complaint> findByCategory(String category);
    List<Complaint> findByUserIdAndStatus(String userId, String status);
    void deleteByUserId(String userId);
}