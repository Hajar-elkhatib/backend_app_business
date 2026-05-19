package ma.ensi.backend_businnes_app.Repository.analysis;

import ma.ensi.backend_businnes_app.Model.analysis.CompetitorAnalysis;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitorAnalysisRepository extends MongoRepository<CompetitorAnalysis, String> {

    List<CompetitorAnalysis> findByProjectId(String projectId);
    List<CompetitorAnalysis> findByMarketAnalysisId(String marketAnalysisId);
    List<CompetitorAnalysis> findByProjectIdAndMarketAnalysisId(String projectId, String marketAnalysisId);

    List<CompetitorAnalysis> findByCompetitorSector(String sector);
    List<CompetitorAnalysis> findByStrengthLevel(String strengthLevel);
    List<CompetitorAnalysis> findByPricePositioning(String pricePositioning);

    long countByProjectId(String projectId);
    void deleteByProjectId(String projectId);
}