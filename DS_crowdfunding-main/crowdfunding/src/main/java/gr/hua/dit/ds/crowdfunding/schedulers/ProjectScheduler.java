package gr.hua.dit.ds.crowdfunding.schedulers;

import gr.hua.dit.ds.crowdfunding.services.ProjectService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProjectScheduler {

    private final ProjectService projectService;

    public ProjectScheduler(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void checkDeadline(){
            System.out.println("[" + LocalDateTime.now() + "] Checking project deadlines");

            projectService.checkProjectDeadlines();
    }
}
