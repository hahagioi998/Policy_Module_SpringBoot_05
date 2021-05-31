package SpringBoot.Policy_Module_Pro_Max.services.implementations;

import SpringBoot.Policy_Module_Pro_Max.models.Policy;
import SpringBoot.Policy_Module_Pro_Max.models.User;
import SpringBoot.Policy_Module_Pro_Max.models.UserInfo;
import SpringBoot.Policy_Module_Pro_Max.repositories.PolicyRepository;
import SpringBoot.Policy_Module_Pro_Max.services.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PolicyServiceImpl implements PolicyService {

    @Autowired
    private PolicyRepository policyRepository;

    @Override
    public List<Policy> getAllPoliciesByCreator(User creator) {
        return policyRepository.findAllByCreator(creator);
    }

    @Override
    public Policy savePolicy(Policy policy) {
        return this.policyRepository.save(policy);
    }

    @Override
    @Cacheable(value = "policies", key = "#id")
    public Policy getPolicyById(Long id) {
        System.out.println("Getting From DB, Policy With Id : " + id);
        Optional<Policy> optional = policyRepository.findById(id);
        Policy policy = null;
        if(optional.isPresent()) {
            policy = optional.get();
        } else {
            throw new RuntimeException("Policy With Id : " + id + " Not Found !");
        }
        return policy;
    }

    @Override
    @CachePut(value = "policies", key = "#policy.id")
    public Policy updatePolicy(Policy policy) {
        Policy existingPolicy = policyRepository.findById(policy.getId()).orElse(null);
        existingPolicy.setPolicyName(policy.getPolicyName());
        existingPolicy.setRemedyType(policy.getRemedyType());
        existingPolicy.setRemedyTime(policy.getRemedyTime());
        existingPolicy.setNotifyUser(policy.getNotifyUser());
        existingPolicy.setNotifyAdmin(policy.getNotifyAdmin());
        existingPolicy.setAdminEmail(policy.getAdminEmail());
        existingPolicy.setAdminEmailSubject(policy.getAdminEmailSubject());
        existingPolicy.setCreator(policy.getCreator());
        existingPolicy.setRisksInvolved(policy.getRisksInvolved());
        return policyRepository.save(existingPolicy);
    }

    @Override
    @CacheEvict(value = "policies", key = "#id")
    public void deletePolicyById(Long id) {
        this.policyRepository.deleteById(id);
    }

    @Override
    public Page<Policy> findPaginated(Integer pageNo, Integer pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.policyRepository.findAllByCreator(userInfo.getUser(), pageable);
    }
}

