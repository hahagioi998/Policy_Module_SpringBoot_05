package SpringBoot.Policy_Module_Pro_Max.repositories;

import SpringBoot.Policy_Module_Pro_Max.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
