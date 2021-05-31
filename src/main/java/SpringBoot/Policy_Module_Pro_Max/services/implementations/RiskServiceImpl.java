package SpringBoot.Policy_Module_Pro_Max.services.implementations;

import SpringBoot.Policy_Module_Pro_Max.models.Policy;
import SpringBoot.Policy_Module_Pro_Max.models.Risk;
import SpringBoot.Policy_Module_Pro_Max.models.User;
import SpringBoot.Policy_Module_Pro_Max.models.UserInfo;
import SpringBoot.Policy_Module_Pro_Max.repositories.RiskRepository;
import SpringBoot.Policy_Module_Pro_Max.services.RiskService;
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
import java.util.Set;

@Service
public class RiskServiceImpl implements RiskService {

    @Autowired
    private RiskRepository riskRepository;

    @Override
    public List<Risk> getAllRisksByCreator(User creator) {
        return this.riskRepository.findAllByCreator(creator);
    }

    @Override
    public List<Risk> getAllRisksByCreatorAndStatus(User creator, Boolean status) {
        return this.riskRepository.findAllByCreatorAndStatus(creator, status);
    }

    @Override
    public Set<Risk> getAllRisksByCreatorAndStatusAndPolicy(User creator, Boolean status, Policy policy) {
        return this.riskRepository.findAllByCreatorAndStatusAndPoliciesIncludedInContaining(creator, status, policy);
    }

    @Override
    public void saveRisk(Risk risk) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        risk.setCreator(userInfo.getUser());
        this.riskRepository.save(risk);
    }

    @Override
    @Cacheable(value = "risks", key = "#id")
    public Risk getRiskById(Long id) {
        System.out.println("Getting From DB, Risk With Id : " + id);
        Optional<Risk> optional = riskRepository.findById(id);
        Risk risk = null;
        if(optional.isPresent()) {
            risk = optional.get();
        } else {
            throw new RuntimeException("Risk With Id : " + id + " Not Found !!");
        }
        return risk;
    }

    @Override
    @CachePut(value = "risks", key = "#updatedRisk.id")
    public Risk updateRisk(Risk updatedRisk) {
        System.out.println("Updating From DB, Risk With Id : " + updatedRisk.getId());
        Optional<Risk> optional = this.riskRepository.findById(updatedRisk.getId());
        if(optional.isPresent()) {
            Risk existingRisk = optional.get();
            existingRisk.setTitle(updatedRisk.getTitle());
            existingRisk.setDescription(updatedRisk.getDescription());
            existingRisk.setKeyWords(updatedRisk.getKeyWords());
            existingRisk.setRegex(updatedRisk.getRegex());
            existingRisk.setStatus(updatedRisk.getStatus());
            return this.riskRepository.save(existingRisk);
        } else {
            throw new RuntimeException("Risk Not Found !");
        }
    }

    @Override
    @CacheEvict(value = "risks", key = "#id")
    public void deleteRiskById(Long id) {
        this.riskRepository.deleteById(id);
    }

    @Override
    public Page<Risk> findPaginated(Integer pageNo, Integer pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.riskRepository.findAllByCreator(userInfo.getUser(), pageable);
    }

    @Override
    public Page<Risk> findPaginatedByStatus(Boolean status, Integer pageNo, Integer pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.riskRepository.findAllByCreatorAndStatus(userInfo.getUser(), status, pageable);
    }
}
