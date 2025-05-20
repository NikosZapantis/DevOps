package gr.hua.dit.ds.crowdfunding.repositories;

import gr.hua.dit.ds.crowdfunding.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByRoleName(String roleName);

    default Role updateOrInsert(Role role) {
        Role existing_role = findByRoleName ( role.getRoleName () ).orElse ( null );
        if ( existing_role != null ) {
            return existing_role;
        } else {
            return save ( role );
        }
    }
}
