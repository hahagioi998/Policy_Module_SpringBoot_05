package SpringBoot.Policy_Module_Pro_Max.repositories;

import SpringBoot.Policy_Module_Pro_Max.models.Policy;
import SpringBoot.Policy_Module_Pro_Max.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
    List<Policy> findAllByCreator(User creator);
    Page<Policy> findAllByCreator(User creator, Pageable pageable);
}
