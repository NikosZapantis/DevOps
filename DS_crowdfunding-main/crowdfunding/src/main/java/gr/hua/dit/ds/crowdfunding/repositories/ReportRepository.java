package gr.hua.dit.ds.crowdfunding.repositories;

import gr.hua.dit.ds.crowdfunding.entities.Project;
import gr.hua.dit.ds.crowdfunding.entities.Report;
import gr.hua.dit.ds.crowdfunding.entities.Role;
import gr.hua.dit.ds.crowdfunding.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

    Optional<Report> findByUserAndProject( User user, Project project);

    //False: Update, True: Save
    default Boolean updateOrInsert(Report report) {
        Report condReport = findByUserAndProject ( report.getUser (), report.getProject () ).orElse ( null );

        if (condReport != null){
            condReport.setDateOfReport ( LocalDateTime.now () );
            condReport.setDescription ( report.getDescription () );
            condReport.setTitle ( report.getTitle () );
            save(condReport);
            return false;
        } else {
            save ( report );
            return true;
        }
    }

}
