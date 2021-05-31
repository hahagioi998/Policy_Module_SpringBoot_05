package SpringBoot.Policy_Module_Pro_Max.services.implementations;

import SpringBoot.Policy_Module_Pro_Max.models.Activity;
import SpringBoot.Policy_Module_Pro_Max.models.User;
import SpringBoot.Policy_Module_Pro_Max.models.UserInfo;
import SpringBoot.Policy_Module_Pro_Max.repositories.ActivityRepository;
import SpringBoot.Policy_Module_Pro_Max.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    @Override
    public void saveActivity(Activity activity) {
        this.activityRepository.save(activity);
    }

    @Override
    public Activity findActivityById(Long id) {
        Optional<Activity> optional = this.activityRepository.findById(id);
        Activity activity = null;

        if(optional.isPresent()) {
            activity = optional.get();
        } else {
            throw new RuntimeException("Activity With Id : " + id + " Not Found !");
        }
        return activity;
    }

    @Override
    public List<Activity> getAllActivity() {
        return this.activityRepository.findAll();
    }

    @Override
    public Activity getActivityById(Long id) {
        Optional<Activity> optional = this.activityRepository.findById(id);
        Activity activity = null;
        if(optional.isPresent()) {
            activity = optional.get();
        } else {
            throw new RuntimeException("Activity With Id : " + id + "Not Found !");
        }
        return activity;
    }

    @Override
    public Page<Activity> findPaginated(Integer pageNo, Integer pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userInfo.getUser();
        return this.activityRepository.findAllByUser(user, pageable);
    }
}
