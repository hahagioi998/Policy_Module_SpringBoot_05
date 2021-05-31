package SpringBoot.Policy_Module_Pro_Max.repositories;

import SpringBoot.Policy_Module_Pro_Max.models.Remedy;
import SpringBoot.Policy_Module_Pro_Max.models.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RemedyRepository extends JpaRepository<Remedy, Long> {
    Remedy findByActivity(Activity activity);
    Page<Remedy> findByActivity(Activity activity, Pageable pageable);
    List<Remedy> findAllByStatusOrderByActionTimeAsc(Boolean status);
    List<Remedy> findAllByStatusAndActionTimeBetween(Boolean status, LocalDateTime actionStartTime, LocalDateTime actionEndTime);
}
