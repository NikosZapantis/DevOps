package gr.hua.dit.ds.crowdfunding.services;

import gr.hua.dit.ds.crowdfunding.entities.Project;
import gr.hua.dit.ds.crowdfunding.entities.Status;
import gr.hua.dit.ds.crowdfunding.repositories.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService( ProjectRepository projectRepository ) {
        this.projectRepository = projectRepository;
    }

    // SELECT * FROM PROJECT;
    @Transactional
    public List<Project> getProjects(){
        return projectRepository.findAll ();
    }

    // SELECT * FROM PROJECT WHERE PROJECTID = <projectID>;
    @Transactional
    public Optional<Project> getProjectById(Integer projectID) {
        return projectRepository.findById(projectID);
    }

    @Transactional
    public void saveProject(Project project){
        projectRepository.save (project);
        System.out.println ("Project saved in the Database!");
    }

    @Transactional
    public boolean deleteProject(Integer projectID){

        if (!projectRepository.existsById ( projectID )){
            System.out.println ("Project with ID: " + projectID + " doesn't exist!");
            return false;
        }

        projectRepository.deleteById ( projectID );
        return true;
    }

    @Transactional
    public void checkProjectDeadlines() {
        for (Project project : getProjects()) {
            if(project.getDeadlineForGoal().isBefore(LocalDateTime.now())){
                project.setStatus(Status.COMPLETED);
                project.setNextStatus(Status.COMPLETED);
                projectRepository.save(project);
            }
        }
    }

   @Transactional
    public Optional<List<Project>> findByStatus(Status status){
        return projectRepository.findByStatus ( status );
   }
}
