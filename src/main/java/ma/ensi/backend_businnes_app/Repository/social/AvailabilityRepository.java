package ma.ensi.backend_businnes_app.Repository.social;

import ma.ensi.backend_businnes_app.Model.social.Availability;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface AvailabilityRepository extends MongoRepository<Availability, String> {
    List<Availability> findBySpecialistId(String specialistId);
    List<Availability> findBySpecialistIdAndStatus(String specialistId, String status);
    List<Availability> findByStatus(String status);
    List<Availability> findByAvailableDateBetween(Date from, Date to);
    void deleteBySpecialistId(String specialistId);
}