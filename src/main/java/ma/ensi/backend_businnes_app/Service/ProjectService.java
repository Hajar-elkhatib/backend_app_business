package ma.ensi.backend_businnes_app.Service;

import ma.ensi.backend_businnes_app.Model.core.Project;
import ma.ensi.backend_businnes_app.Repository.core.ProjectRepository;
import ma.ensi.backend_businnes_app.DTOS.request.CreateProjectRequest;
import ma.ensi.backend_businnes_app.DTOS.request.UpdateProjectRequest;
import ma.ensi.backend_businnes_app.DTOS.response.ProjectResponse;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    public ProjectResponse createProject(String entrepreneurId,
                                         CreateProjectRequest request) {
        Project project = new Project();
        project.setEntrepreneurId(entrepreneurId);
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setSector(request.getSector());
        project.setCountry(request.getCountry());
        project.setCountryCode(request.getCountryCode());
        project.setRegion(request.getRegion());
        project.setFounderExperienceYears(request.getFounderExperienceYears());
        project.setFundingRounds(request.getFundingRounds());
        project.setTeamSize(request.getTeamSize());
        project.setMarketSizeBillion(request.getMarketSizeBillion());
        project.setMarketGrowthRatePercent(request.getMarketGrowthRatePercent());
        project.setProductTractionUsers(request.getProductTractionUsers());
        project.setBurnRateMillion(request.getBurnRateMillion());
        project.setRevenueMillion(request.getRevenueMillion());
        project.setInvestorType(request.getInvestorType());
        project.setCompetitionLevel(request.getCompetitionLevel());
        project.setSearchTrendScore(request.getSearchTrendScore());
        project.setUserWordBank(request.isUserWordBank());
        project.setOpinions(request.getOpinions());
        project.setProjectStatus("DRAFT");
        project.setCreatedAt(new Date());

        Project saved = projectRepository.save(project);
        return mapToResponse(saved);
    }


    public ProjectResponse getProjectById(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        return mapToResponse(project);
    }


    public List<ProjectResponse> getProjectsByEntrepreneur(String entrepreneurId) {
        return projectRepository.findByEntrepreneurId(entrepreneurId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public ProjectResponse updateProject(String projectId,
                                         UpdateProjectRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (request.getTitle() != null) project.setTitle(request.getTitle());
        if (request.getDescription() != null) project.setDescription(request.getDescription());
        if (request.getSector() != null) project.setSector(request.getSector());
        if (request.getCountry() != null) project.setCountry(request.getCountry());
        if (request.getCountryCode() != null) project.setCountryCode(request.getCountryCode());
        if (request.getRegion() != null) project.setRegion(request.getRegion());
        if (request.getInvestorType() != null) project.setInvestorType(request.getInvestorType());
        if (request.getCompetitionLevel() != null) project.setCompetitionLevel(request.getCompetitionLevel());
        if (request.getOpinions() != null) project.setOpinions(request.getOpinions());
        if (request.getTeamSize() != 0) project.setTeamSize(request.getTeamSize());
        if (request.getFundingRounds() != 0) project.setFundingRounds(request.getFundingRounds());
        if (request.getFounderExperienceYears() != 0) project.setFounderExperienceYears(request.getFounderExperienceYears());
        if (request.getMarketSizeBillion() != 0) project.setMarketSizeBillion(request.getMarketSizeBillion());
        if (request.getMarketGrowthRatePercent() != 0) project.setMarketGrowthRatePercent(request.getMarketGrowthRatePercent());
        if (request.getProductTractionUsers() != 0) project.setProductTractionUsers(request.getProductTractionUsers());
        if (request.getBurnRateMillion() != 0) project.setBurnRateMillion(request.getBurnRateMillion());
        if (request.getRevenueMillion() != 0) project.setRevenueMillion(request.getRevenueMillion());
        if (request.getSearchTrendScore() != 0) project.setSearchTrendScore(request.getSearchTrendScore());

        Project saved = projectRepository.save(project);
        return mapToResponse(saved);
    }


    public void deleteProject(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        projectRepository.delete(project);
    }


    public ProjectResponse submitProject(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getProjectStatus().equals("DRAFT")) {
            throw new RuntimeException("Only DRAFT projects can be submitted");
        }

        project.setProjectStatus("SUBMITTED");
        Project saved = projectRepository.save(project);
        return mapToResponse(saved);
    }


    private ProjectResponse mapToResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setEntrepreneurId(project.getEntrepreneurId());
        response.setTitle(project.getTitle());
        response.setDescription(project.getDescription());
        response.setProjectStatus(project.getProjectStatus());
        response.setSector(project.getSector());
        response.setCountry(project.getCountry());
        response.setCountryCode(project.getCountryCode());
        response.setRegion(project.getRegion());
        response.setFounderExperienceYears(project.getFounderExperienceYears());
        response.setFundingRounds(project.getFundingRounds());
        response.setTeamSize(project.getTeamSize());
        response.setMarketSizeBillion(project.getMarketSizeBillion());
        response.setMarketGrowthRatePercent(project.getMarketGrowthRatePercent());
        response.setProductTractionUsers(project.getProductTractionUsers());
        response.setBurnRateMillion(project.getBurnRateMillion());
        response.setRevenueMillion(project.getRevenueMillion());
        response.setInvestorType(project.getInvestorType());
        response.setCompetitionLevel(project.getCompetitionLevel());
        response.setSearchTrendScore(project.getSearchTrendScore());
        response.setUserWordBank(project.isUserWordBank());
        response.setOpinions(project.getOpinions());
        response.setCreatedAt(project.getCreatedAt());
        return response;
    }
}