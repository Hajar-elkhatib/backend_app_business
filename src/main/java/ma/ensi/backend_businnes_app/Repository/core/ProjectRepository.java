

package ma.ensi.backend_businnes_app.Repository.core;

import ma.ensi.backend_businnes_app.Model.core.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<Project, String> {

    // By entrepreneur
    List<Project> findByEntrepreneurId(String entrepreneurId);
    Optional<Project> findByIdAndEntrepreneurId(String id, String entrepreneurId);

    // By status
    List<Project> findByProjectStatus(String projectStatus);
    List<Project> findByEntrepreneurIdAndProjectStatus(String entrepreneurId, String projectStatus);

    // By sector / location
    List<Project> findBySector(String sector);
    List<Project> findByCountry(String country);
    List<Project> findByRegion(String region);
    List<Project> findBySectorAndCountry(String sector, String country);

    // By investor type & competition
    List<Project> findByInvestorType(String investorType);
    List<Project> findByCompetitionLevel(String competitionLevel);

    // Metrics filters
    List<Project> findByMarketSizeBillionGreaterThanEqual(double minMarketSize);
    List<Project> findByRevenueMillionGreaterThanEqual(double minRevenue);
    List<Project> findByTeamSizeGreaterThanEqual(int minTeamSize);

    // Counts
    long countByEntrepreneurId(String entrepreneurId);
    long countByProjectStatus(String projectStatus);

    boolean existsByIdAndEntrepreneurId(String id, String entrepreneurId);
}
