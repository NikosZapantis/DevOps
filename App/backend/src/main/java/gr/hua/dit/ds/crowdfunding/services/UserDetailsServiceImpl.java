package gr.hua.dit.ds.crowdfunding.services;

import gr.hua.dit.ds.crowdfunding.entities.Role;
import gr.hua.dit.ds.crowdfunding.entities.User;
import gr.hua.dit.ds.crowdfunding.repositories.RoleRepository;
import gr.hua.dit.ds.crowdfunding.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private BCryptPasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> opt = userRepository.findByUsername(username);

        if(opt.isEmpty())
            throw new UsernameNotFoundException("User with username: " + username +" not found");
        else {
            User user = opt.get();
            return UserDetailsImpl.build(user);
        }
    }

    @Transactional
    public Integer saveUser(User user) {
        String passwd = user.getPassword();
        String encodedPassword = passwordEncoder.encode(passwd);
        user.setPassword(encodedPassword);

        Role role = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));
        Set<Role> roles = new HashSet<> ();
        roles.add(role);
        user.setRoles(roles);

        user = userRepository.save(user);
        return user.getUserID ();
    }

    @Transactional
    public Integer updateUser(User user) {
        user = userRepository.save(user);
        return user.getUserID();
    }

    @Transactional
    public Object getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUser(Integer userId) {
        return userRepository.findByUserID(userId);
    }

    @Transactional
    public void updateOrInsertRole(Role role) {
        roleRepository.updateOrInsert(role);
    }

}
