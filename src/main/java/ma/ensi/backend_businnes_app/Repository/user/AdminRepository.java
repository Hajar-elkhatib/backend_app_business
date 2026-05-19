package ma.ensi.backend_businnes_app.Repository.user;

import ma.ensi.backend_businnes_app.Model.auth.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {

    Optional<Admin> findByUserId(String userId);
    boolean existsByUserId(String userId);
    void deleteByUserId(String userId);
}