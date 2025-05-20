package gr.hua.dit.ds.crowdfunding.controllers;

import gr.hua.dit.ds.crowdfunding.entities.Fund;
import gr.hua.dit.ds.crowdfunding.entities.Project;
import gr.hua.dit.ds.crowdfunding.services.FundService;
import gr.hua.dit.ds.crowdfunding.services.ProjectService;
import gr.hua.dit.ds.crowdfunding.services.UserDetailsImpl;
import gr.hua.dit.ds.crowdfunding.services.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/fund")
public class FundController {
    ProjectService projectService;
    FundService fundService;
    UserDetailsServiceImpl userService;

    public FundController(ProjectService projectService, FundService fundService, UserDetailsServiceImpl userService) {
        this.projectService = projectService;
        this.fundService = fundService;
        this.userService = userService;
    }

    @Secured("ROLE_USER")
    @PostMapping("/{id}/new")
    public ResponseEntity<String> addFund(@PathVariable int id, @Valid @RequestBody Fund fund, @AuthenticationPrincipal UserDetailsImpl auth) {
        //set default value for field without @JsonIgnore
        fund.setDateOfTransaction(LocalDateTime.now());
        fund.setUser(userService.getUser(auth.getId()).get());

        if(fundService.assignProjectToFund(fund, id)) {
            return ResponseEntity.ok("Fund successfully added to project");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to add fund, no active project found");
        }
    }

    @Secured("ROLE_USER")
    @GetMapping("/{id}/all")
    public ResponseEntity<List<Fund>> getProjectFunds(@AuthenticationPrincipal UserDetailsImpl auth, @PathVariable int id) {
        Optional<Project> project = projectService.getProjectById(id);

        if(project.isPresent()) {
            List<Fund> funds = new ArrayList<>(project.get().getFunds());

            //If the user is NOT the project's creator or the supporter filter out 'private' funds
            if(!auth.getUsername().equals(project.get().getOrganizer().getUsername()) ) {
                funds.removeIf(fund -> !fund.getPublic() && !Objects.equals(auth.getId(), fund.getUser().getUserID()));
            }

            return ResponseEntity.ok(funds);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
