package SpringBoot.Policy_Module_Pro_Max.services;

import SpringBoot.Policy_Module_Pro_Max.models.Activity;
import SpringBoot.Policy_Module_Pro_Max.models.ActivityDetail;
import org.springframework.data.domain.Page;

public interface ActivityDetailService {
    void saveActivityDetail(ActivityDetail activityDetail);
    Page<ActivityDetail> findPaginatedByActivity(Activity activity, Integer pageNo, Integer pageSize, String sortField, String sortDirection);
}
