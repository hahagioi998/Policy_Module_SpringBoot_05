package SpringBoot.Policy_Module_Pro_Max.services.implementations;

import SpringBoot.Policy_Module_Pro_Max.models.Remedy;
import SpringBoot.Policy_Module_Pro_Max.models.Activity;
import SpringBoot.Policy_Module_Pro_Max.repositories.RemedyRepository;
import SpringBoot.Policy_Module_Pro_Max.services.RemedyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RemedyServiceImpl implements RemedyService {

    @Autowired
    private RemedyRepository remedyRepository;

    @Override
    public void saveRemedy(Remedy action) {
        this.remedyRepository.save(action);
    }

    @Override
    public Remedy getRemedyByActivity(Activity activity) {
        return this.remedyRepository.findByActivity(activity);
    }

    @Override
    public List<Remedy> getRemedyByStatusAndTime(Boolean status, LocalDateTime startTime, LocalDateTime endTime) {
        return this.remedyRepository.findAllByStatusAndActionTimeBetween(status, startTime, endTime);
    }

    @Override
    public void updateRemedyStatusById(Long id) {
        Optional<Remedy> optional = this.remedyRepository.findById(id);
        Remedy currentRemedy = null;
        if(optional.isPresent()) {
            currentRemedy = optional.get();
        } else {
            throw new RuntimeException("Action With This Id Not Found !");
        }
        currentRemedy.setStatus(true);
        this.remedyRepository.save(currentRemedy);
    }
}
