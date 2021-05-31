package SpringBoot.Policy_Module_Pro_Max.services;

import SpringBoot.Policy_Module_Pro_Max.dto.UserRegistrationDto;
import SpringBoot.Policy_Module_Pro_Max.models.User;

import java.util.List;

public interface UserService {
    List<User> findAll();
    User findByUsername(String username);
    User save(UserRegistrationDto registration);
}