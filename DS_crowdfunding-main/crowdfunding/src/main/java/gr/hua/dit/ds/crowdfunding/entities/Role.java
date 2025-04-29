package gr.hua.dit.ds.crowdfunding.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table (name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer roleID;

    @Column(length = 20)
    @Size(max = 20)
    @NotBlank
    private String roleName;

    public Role(){

    }

    public Role( String roleName ) {
        this.roleName = roleName;
    }

    public Integer getRoleID() {
        return roleID;
    }

    public void setRoleID( Integer roleID ) {
        this.roleID = roleID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName( String roleName ) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return roleName;
    }
}
