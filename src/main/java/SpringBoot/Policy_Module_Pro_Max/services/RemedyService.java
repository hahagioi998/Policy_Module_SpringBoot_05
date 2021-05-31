package SpringBoot.Policy_Module_Pro_Max.services;

import SpringBoot.Policy_Module_Pro_Max.models.Activity;
import SpringBoot.Policy_Module_Pro_Max.models.Remedy;

import java.time.LocalDateTime;
import java.util.List;

public interface RemedyService {
    void saveRemedy(Remedy action);
    Remedy getRemedyByActivity(Activity activity);
    List<Remedy> getRemedyByStatusAndTime(Boolean status, LocalDateTime startTime, LocalDateTime endTime);
    void updateRemedyStatusById(Long id);
}

