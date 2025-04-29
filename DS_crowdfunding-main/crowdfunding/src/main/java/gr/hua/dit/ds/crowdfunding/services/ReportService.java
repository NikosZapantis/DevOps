package gr.hua.dit.ds.crowdfunding.services;

import gr.hua.dit.ds.crowdfunding.entities.Project;
import gr.hua.dit.ds.crowdfunding.entities.Report;
import gr.hua.dit.ds.crowdfunding.entities.Status;
import gr.hua.dit.ds.crowdfunding.repositories.ProjectRepository;
import gr.hua.dit.ds.crowdfunding.repositories.ReportRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    private ReportRepository reportRepository;
    private ProjectService projectService;

    public ReportService( ReportRepository reportRepository, ProjectService projectService ) {
        this.reportRepository = reportRepository;
        this.projectService = projectService;
    }

    // SELECT * FROM REPORT;
    @Transactional
    public List<Report> getReport(){
        return reportRepository.findAll ();
    }

    // SELECT * FROM REPORT WHERE REPORTID = <reportID>;
    @Transactional
    public Optional<Report> getReportByID( Integer reportID){
        return reportRepository.findById ( reportID );
    }

    @Transactional
    public void saveReport(Report report){

        if (reportRepository.updateOrInsert ( report )){
            System.out.println ("Report saved in the Database!");
        } else {
            System.out.println ("Report was updated!");
        }
    }

    @Transactional
    public boolean deleteReportByID(Integer reportID){

        if (!reportRepository.existsById ( reportID )){
            System.out.println ("Report with ID: " + reportID + " doesn't exist!");
            return false;
        }

        reportRepository.deleteById ( reportID );
        System.out.println ("Report with ID: " + reportID + " deleted!");
        return true;
    }

    @Transactional
    public boolean assignProjectToReport(Report report, Integer projectID){
        Optional<Project> project =  projectService.getProjectById( projectID );

        if( project.isEmpty () || !project.get().getStatus ().equals ( Status.ACTIVE )){
            return false;
        }

        Project validProject = project.get ();
        report.setProject( validProject );
        saveReport ( report );
        return true;
    }

    @Transactional
    public Optional<List<Report>> findByProjectID(Integer projectID){
        Optional<Project> project = projectService.getProjectById(projectID);

        if (project.isEmpty()) {
            return Optional.empty();
        }

        Project existingProject = project.get();
        return Optional.ofNullable(existingProject.getReports());
    }
}
