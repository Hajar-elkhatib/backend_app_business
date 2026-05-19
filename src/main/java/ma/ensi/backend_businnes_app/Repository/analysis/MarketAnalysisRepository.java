package ma.ensi.backend_businnes_app.Repository.analysis;

import ma.ensi.backend_businnes_app.Model.analysis.MarketAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketAnalysisRepository extends MongoRepository<MarketAnalysis, String> {

    List<MarketAnalysis> findByProjectId(String projectId);
    Optional<MarketAnalysis> findFirstByProjectIdOrderByCreatedAtDesc(String projectId);

    List<MarketAnalysis> findBySector(String sector);
    List<MarketAnalysis> findByRegion(String region);
    List<MarketAnalysis> findByCountryCode(String countryCode);
    List<MarketAnalysis> findByCompetitionLevel(String competitionLevel);
    List<MarketAnalysis> findByPriority(String priority);
    List<MarketAnalysis> findByModelVersion(String modelVersion);

    List<MarketAnalysis> findByGrowthRateGreaterThanEqual(double minGrowthRate);
    List<MarketAnalysis> findByMarketSizeGreaterThanEqual(double minMarketSize);
    List<MarketAnalysis> findByConfidenceScoreGreaterThanEqual(double minConfidence);

    boolean existsByProjectId(String projectId);
    long countByProjectId(String projectId);
    void deleteByProjectId(String projectId);
}