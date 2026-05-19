package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.DTOS.request.CreateProjectRequest;
import ma.ensi.backend_businnes_app.DTOS.request.UpdateProjectRequest;
import ma.ensi.backend_businnes_app.DTOS.response.ProjectResponse;
import ma.ensi.backend_businnes_app.Service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @PostMapping("/{entrepreneurId}")
    public ResponseEntity<ProjectResponse> createProject(
            @PathVariable String entrepreneurId,
            @RequestBody CreateProjectRequest request) {
        return ResponseEntity.ok(projectService.createProject(entrepreneurId, request));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable String id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }


    @GetMapping("/entrepreneur/{entrepreneurId}")
    public ResponseEntity<List<ProjectResponse>> getProjectsByEntrepreneur(
            @PathVariable String entrepreneurId) {
        return ResponseEntity.ok(projectService.getProjectsByEntrepreneur(entrepreneurId));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable String id,
            @RequestBody UpdateProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(
            @PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok("Project deleted successfully");
    }


    @PutMapping("/{id}/submit")
    public ResponseEntity<ProjectResponse> submitProject(
            @PathVariable String id) {
        return ResponseEntity.ok(projectService.submitProject(id));
    }
}