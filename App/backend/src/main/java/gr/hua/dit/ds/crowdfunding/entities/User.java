package gr.hua.dit.ds.crowdfunding.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table( name = "users", uniqueConstraints = {
        @UniqueConstraint ( columnNames = "username"),
        @UniqueConstraint ( columnNames = "email")
        })
public class User {

    // ------------------- Attributes ------------------------------


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userID;

    @NotBlank
    @Column(length = 20)
    @Size(max = 20)
    private String username;

    @NotBlank
    @Column(length = 30)
    @Size(max = 30)
    private String firstName;

    @NotBlank
    @Column(length = 30)
    @Size(max = 30)
    private String lastName;

    @NotBlank
    @Column(length = 60)
    @Size(max = 60)
    @Email
    private String email;

    @JsonIgnore
    @NotBlank
    @Column(length = 90)
    @Size(max = 90)
    private String password;

    @Column
    private LocalDateTime dateOfRegistration = LocalDateTime.now ();

    // ------------------- Relationships ------------------------------

    @JsonIgnore
    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> reports;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fund> funds;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable( name = "user_roles",
            joinColumns = @JoinColumn(name = "userID"),
            inverseJoinColumns = @JoinColumn(name = "roleID"))
    private Set<Role> roles = new HashSet<> ();

    // ------------------- Methods -----------------------------------


    public User( String username, String firstName, String lastName, String email, String password) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public User(){

    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID( Integer userID ) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName( String firstName ) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }

    public LocalDateTime getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration( LocalDateTime dateOfRegistration ) {
        this.dateOfRegistration = dateOfRegistration;
    }


    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles( Set<Role> roles ) {
        this.roles = roles;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects( List<Project> projects ) {
        this.projects = projects;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports( List<Report> reports ) {
        this.reports = reports;
    }

    public List<Fund> getFunds() {
        return funds;
    }

    public void setFunds( List<Fund> funds ) {
        this.funds = funds;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", dateOfRegistration=" + dateOfRegistration +
                '}';
    }
}
