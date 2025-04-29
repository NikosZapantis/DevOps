package gr.hua.dit.ds.crowdfunding.entities;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class Project {

    // ------------------- Attributes ------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Integer projectID;

    @Column
    @Lob
    private String image;

    @Column
    private String title;

    @Column
    private String description;

    //@JsonIgnore
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    //@JsonIgnore
    @Enumerated(EnumType.STRING)
    private Status nextStatus = Status.ACTIVE;

    @Min(value = 1000, message = "Goal amount must be at least 1000 euro")
    @Column
    private float goalAmount;

    @Column
    private float currentAmount = 0;

    @Column
    private LocalDateTime dateOfCreation = LocalDateTime.now();

    @Column
    private LocalDateTime deadlineForGoal;


    // ------------------- Relationships ------------------------------

    //@JsonIgnore
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "userID")
    private User organizer;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fund> funds;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;

    // ------------------- Methods -----------------------------------

    @PrePersist
    @PreUpdate
    public void isValidDeadlineForGoal(){
        if (deadlineForGoal.isAfter ( dateOfCreation.plusMonths ( 1 ) )){
            return;
        }

        setDeadlineForGoal ( LocalDateTime.now ().plusMonths ( 1 ) );
    }

    public Project( String title, String description, float goalAmount, LocalDateTime dateOfCreation, LocalDateTime deadlineForGoal ) {
        this.title = title;
        this.description = description;
        this.goalAmount = goalAmount;
        this.dateOfCreation = dateOfCreation;
        this.deadlineForGoal = deadlineForGoal;
    }


    public Project(){

    }

    public Integer getProjectID() {
        return projectID;
    }

    public void setProjectID( int projectID ) {
        this.projectID = projectID;
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

    public float getGoalAmount() {
        return goalAmount;
    }

    public void setGoalAmount( float goalAmount ) {
        this.goalAmount = goalAmount;
    }

    public float getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount( float currentAmount ) {
        this.currentAmount = currentAmount;
    }

    public LocalDateTime getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation( LocalDateTime dateOfCreation ) {
        this.dateOfCreation = dateOfCreation;
    }

    public LocalDateTime getDeadlineForGoal() {
        return deadlineForGoal;
    }

    public void setDeadlineForGoal( LocalDateTime deadlineForGoal ) {
        this.deadlineForGoal = deadlineForGoal;
    }

    public void setProjectID( Integer projectID ) {
        this.projectID = projectID;
    }

    public List<Fund> getFunds() {
        return funds;
    }

    public void setFunds( List<Fund> funds ) {
        this.funds = funds;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports( List<Report> reports ) {
        this.reports = reports;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer( User organizer ) {
        this.organizer = organizer;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus( Status status ) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectID=" + projectID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", goalAmount=" + goalAmount +
                ", currentAmount=" + currentAmount +
                ", dateOfCreation=" + dateOfCreation +
                ", deadlineForGoal=" + deadlineForGoal +
                ", funds=" + funds +
                ", reports=" + reports +
                '}';
    }

    public String getImage() {
        return image;
    }

    public void setImage( String image ) {
        this.image = image;
    }

    public Status getNextStatus() {
        return nextStatus;
    }

    public void setNextStatus(Status nextStatus) {
        this.nextStatus = nextStatus;
    }
}
