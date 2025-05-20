package gr.hua.dit.ds.crowdfunding.controllers;

import gr.hua.dit.ds.crowdfunding.entities.Project;
import gr.hua.dit.ds.crowdfunding.entities.Status;
import gr.hua.dit.ds.crowdfunding.entities.User;
import gr.hua.dit.ds.crowdfunding.services.FundService;
import gr.hua.dit.ds.crowdfunding.services.ProjectService;
import gr.hua.dit.ds.crowdfunding.services.UserDetailsImpl;
import gr.hua.dit.ds.crowdfunding.services.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/project")
public class ProjectController {
    ProjectService projectService;
    FundService fundService;
    UserDetailsServiceImpl userService;

    public ProjectController(ProjectService projectService, FundService fundService, UserDetailsServiceImpl userService) {
        this.projectService = projectService;
        this.fundService = fundService;
        this.userService = userService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable int id) {
        Optional<Project> project = projectService.getProjectById(id);
        return project.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Secured("ROLE_USER")
    @PostMapping("/new")
    public void addNewProject(@Valid @RequestBody Project project, @AuthenticationPrincipal UserDetailsImpl auth) {
        //set default values for fields without @JsonIgnore
        project.setStatus(Status.PENDING);
        project.setNextStatus(Status.ACTIVE);
        project.setDateOfCreation(LocalDateTime.now());
        project.setCurrentAmount(0);
        project.setOrganizer(userService.getUser(auth.getId()).get());

        projectService.saveProject(project);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteProject(@PathVariable int id) {
        if(projectService.deleteProject(id)) {
            return ResponseEntity.ok("Project deleted");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}/update-status")
    public ResponseEntity<String> stopProject(@PathVariable int id) {
        Optional<Project> projectOpt = projectService.getProjectById(id);

        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setStatus(project.getNextStatus());

            switch (project.getStatus()){
                case ACTIVE:
                    project.setNextStatus(Status.STOPPED);
                    break;
                case STOPPED:
                    project.setNextStatus(Status.ACTIVE);
                    break;
                default:
                    break;
            }

            projectService.saveProject(project);
            return ResponseEntity.ok().body("Project status has been updated to: [" + project.getStatus() +"]");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
    }

    @GetMapping("/all/{status}")
    public ResponseEntity<List<Project>> getPendingProjects(@PathVariable Status status) {
        return ResponseEntity.ok(projectService.findByStatus ( status ).get());
    }

    @Secured("ROLE_USER")
    @PutMapping("/{id}/desc-update")
    public ResponseEntity<String> updateProjectDescription(@PathVariable int id, @RequestBody String updateText, @AuthenticationPrincipal UserDetailsImpl auth) {
        Optional<Project> projectOpt = projectService.getProjectById(id);

        if(projectOpt.isPresent()){
            Project project = projectOpt.get();
            if(auth.getId().equals(project.getOrganizer().getUserID())){
                project.setDescription(project.getDescription() + "\n[Update] " + updateText);

                projectService.saveProject(project);
                return ResponseEntity.ok("Project updated");
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not the project's creator");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found");
    }

    @Secured("ROLE_USER")
    @GetMapping("/personal")
    public ResponseEntity<List<Project>> getPersonalProjects(@AuthenticationPrincipal UserDetailsImpl auth) {
        Optional<User> user = userService.getUser(auth.getId());
        return user.map(value -> ResponseEntity.ok(value.getProjects())).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
