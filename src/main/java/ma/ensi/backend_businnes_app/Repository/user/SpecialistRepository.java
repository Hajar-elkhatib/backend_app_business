package ma.ensi.backend_businnes_app.Repository.user;


import ma.ensi.backend_businnes_app.Model.auth.Specialist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialistRepository extends MongoRepository<Specialist, String> {

    Optional<Specialist> findByUserId(String userId);
    boolean existsByUserId(String userId);
    void deleteByUserId(String userId);

    List<Specialist> findByProfession(String profession);
    List<Specialist> findByExpertiseDomain(String expertiseDomain);
    List<Specialist> findBySectorsContaining(String sector);
    List<Specialist> findBySkillsContaining(String skill);
    List<Specialist> findByAvailabilityStatus(String availabilityStatus);
    List<Specialist> findByLocation(String location);
    List<Specialist> findByRatingGreaterThanEqual(double minRating);
    List<Specialist> findByHourlyRateBetween(double min, double max);
}