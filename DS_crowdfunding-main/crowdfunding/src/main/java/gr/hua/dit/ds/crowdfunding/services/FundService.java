package gr.hua.dit.ds.crowdfunding.services;

import gr.hua.dit.ds.crowdfunding.entities.Fund;
import gr.hua.dit.ds.crowdfunding.entities.Project;
import gr.hua.dit.ds.crowdfunding.entities.Status;
import gr.hua.dit.ds.crowdfunding.repositories.FundRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FundService {

    private final ProjectService projectService;
    private FundRepository fundRepository;

    public FundService(FundRepository fundRepository, ProjectService projectService) {
        this.fundRepository = fundRepository;
        this.projectService = projectService;
    }

//    // SELECT * FROM FUND;
//    @Transactional
//    public List<Fund> getFunds(){
//        return fundRepository.findAll ();
//    }
//
    // SELECT * FROM FUND WHERE FUNDID = <fundID>;
    @Transactional
    public Optional<Fund> getFundByID( Integer fundID){
        return fundRepository.findById ( fundID );
    }

    @Transactional
    public void saveFund(Fund fund){
        fundRepository.save(fund);
        System.out.println ("Fund saved in the Database!");
    }

    @Transactional
    public boolean deleteFund(Integer fundID){

        if (!fundRepository.existsById ( fundID )){
            System.out.println ("Fund with ID: " + fundID + " doesn't exist!");
            return false;
        }

        fundRepository.deleteById ( fundID );
        System.out.println ("Fund with ID: " + fundID + " deleted!");
        return true;
    }

//    // Assigning a Project to the Fund Table
//    @Transactional
//    public boolean assignProjectToFund(Integer fundID, Project project){
//        Optional<Fund> fund = getFundByID ( fundID );
//
//        if ( fund.isEmpty () ){
//            return false;
//        }
//
//        Fund existingFund = fund.get ();
//        existingFund.setProject ( project );
//
//        //Add the pledge to the project's current goal
//        project.setCurrentAmount(project.getCurrentAmount() + existingFund.getAmount());
//
//        saveFund ( existingFund );
//
//        return true;
//
////        System.out.println (fund);
////        System.out.println (fund.getProject ());
////        fund.setProject ( project );
////        System.out.println (fund.getProject ());
////        saveFund ( fund );
//    }

    @Transactional
    public boolean assignProjectToFund(Fund fund, int projectID){
        Optional<Project> project = projectService.getProjectById( projectID );

        //If the project exists and its status is ACTIVE then save the fund
        if ( project.isEmpty () || !project.get().getStatus().equals(Status.ACTIVE)){
            return false;
        }

        Project validProject = project.get();
        fund.setProject ( validProject );

        //Add the pledge to the project's current goal
        validProject.setCurrentAmount(validProject.getCurrentAmount() + fund.getAmount());

        projectService.saveProject ( validProject );
        saveFund ( fund );
        return true;
    }
}
