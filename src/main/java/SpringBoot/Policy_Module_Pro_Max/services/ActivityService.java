package SpringBoot.Policy_Module_Pro_Max.services;

import SpringBoot.Policy_Module_Pro_Max.models.Activity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ActivityService {
    void saveActivity(Activity activity);
    Activity findActivityById(Long id);
    List<Activity> getAllActivity();
    Activity getActivityById(Long id);
    Page<Activity> findPaginated(Integer pageNo, Integer pageSize, String sortField, String sortDir);
}