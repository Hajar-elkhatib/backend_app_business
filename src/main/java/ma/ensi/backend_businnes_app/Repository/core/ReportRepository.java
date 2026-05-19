package ma.ensi.backend_businnes_app.Repository.core;

import ma.ensi.backend_businnes_app.Model.core.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {

    // By project
    List<Report> findByProjectId(String projectId);
    Optional<Report> findFirstByProjectIdOrderByCreatedAtDesc(String projectId);

    // By type
    List<Report> findByReportType(String reportType);
    List<Report> findByProjectIdAndReportType(String projectId, String reportType);

    // By region
    List<Report> findByRegion(String region);

    // By model version
    List<Report> findByModelVersion(String modelVersion);

    // Checks
    boolean existsByProjectId(String projectId);
    long countByProjectId(String projectId);

    void deleteByProjectId(String projectId);
}