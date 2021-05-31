package SpringBoot.Policy_Module_Pro_Max.services.implementations;

import SpringBoot.Policy_Module_Pro_Max.dto.UserRegistrationDto;
import SpringBoot.Policy_Module_Pro_Max.models.Role;
import SpringBoot.Policy_Module_Pro_Max.repositories.UserRepository;
import SpringBoot.Policy_Module_Pro_Max.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import SpringBoot.Policy_Module_Pro_Max.models.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<User> findAll() { return userRepository.findAll(); }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User save(UserRegistrationDto registration) {
        User user = new User();
        user.setFirstName(registration.getFirstName());
        user.setLastName(registration.getLastName());
        user.setUsername(registration.getUsername());
        user.setEmail(registration.getEmail());
        user.setFileDirectory( "/files/" + registration.getUsername());
        File currentDir = new File(".");
        Path path = Paths.get(currentDir + user.getFileDirectory());
        Path pathMove = Paths.get(currentDir + user.getFileDirectory() + "/moved");
        Path pathRename = Paths.get(currentDir + user.getFileDirectory() + "/renamed");
        Path pathNotify = Paths.get(currentDir + user.getFileDirectory() + "/notified");
        Path pathRemedy = Paths.get(currentDir + user.getFileDirectory() + "/remedy");
        try {
            Files.createDirectories(path);
            Files.createDirectories(pathMove);
            Files.createDirectories(pathRename);
            Files.createDirectories(pathNotify);
            Files.createDirectories(pathRemedy);
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setPassword(passwordEncoder.encode(registration.getPassword()));
        Set<Role> set = new HashSet<>();
        set.add(new Role("ROLE_USER"));
        // user.addRoles(set); // Bidirectional Sync : Not Working !? A Unidirectional ManyToMany ???
        user.setRoles(set);
        return userRepository.save(user);
    }

    private Collection< ? extends GrantedAuthority> mapRolesToAuthorities(Collection <Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

}
