package SpringBoot.Policy_Module_Pro_Max.repositories;

import SpringBoot.Policy_Module_Pro_Max.models.Activity;
import SpringBoot.Policy_Module_Pro_Max.models.ActivityDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityDetailRepository extends JpaRepository<ActivityDetail, Long> {
    Page<ActivityDetail> findAllByActivity(Activity activity, Pageable pageable);
}
