package ma.ensi.backend_businnes_app.Repository.user;
import ma.ensi.backend_businnes_app.Model.auth.Entrepreneur;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntrepreneurRepository extends MongoRepository<Entrepreneur, String> {

    Optional<Entrepreneur> findByUserId(String userId);
    boolean existsByUserId(String userId);
    void deleteByUserId(String userId);

    List<Entrepreneur> findByBusinessType(String businessType);
    List<Entrepreneur> findByCompanyName(String companyName);
}