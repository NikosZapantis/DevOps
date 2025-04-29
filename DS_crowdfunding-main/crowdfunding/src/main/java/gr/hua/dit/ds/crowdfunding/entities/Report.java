package gr.hua.dit.ds.crowdfunding.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userID", "projectID"}))
public class Report {

    // ------------------- Attributes ------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer reportID;

    @Column(length = 60)
    @Size(max = 60)
    private String title;

    @Column(length = 200)
    @Size(max = 200)
    private String description;

    @Column
    private LocalDateTime dateOfReport = LocalDateTime.now ();


    // ------------------- Relationships ------------------------------

    //@JsonIgnore
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "userID")
    private User user;

    @JsonIgnore
    @ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE,
            CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "projectID")
    private Project project;

    // ------------------- Methods -----------------------------------

    public Report( String title, String description, LocalDateTime dateOfReport) {
        this.title = title;
        this.description = description;
        this.dateOfReport = dateOfReport;
    }

    public Report(){
    }

    public Integer getReportID() {
        return reportID;
    }

    public void setReportID( Integer reportID ) {
        this.reportID = reportID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public LocalDateTime getDateOfReport() {
        return dateOfReport;
    }

    public void setDateOfReport( LocalDateTime dateOfReport ) {
        this.dateOfReport = dateOfReport;
    }

    public Project getProject() {
        return project;
    }

    public void setProject( Project project ) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser( User user ) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportID=" + reportID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dateOfReport=" + dateOfReport +
                ", project=" + project.getProjectID () +
                '}';
    }
}
