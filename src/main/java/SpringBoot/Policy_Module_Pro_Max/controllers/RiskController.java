package SpringBoot.Policy_Module_Pro_Max.controllers;

import SpringBoot.Policy_Module_Pro_Max.models.Policy;
import SpringBoot.Policy_Module_Pro_Max.models.Risk;
import SpringBoot.Policy_Module_Pro_Max.services.RiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/risk")
public class RiskController {

    @Autowired
    private RiskService riskService;

    // Display List of All Risks Created By The Current Logged In User
    @GetMapping
    public String viewRiskHomePage(Model model) {
        return findPaginated(1, "title", "asc", model);
    }

    // For Adding A New Risk
    @GetMapping("/showNewRiskForm")
    public String showNewRiskForm(Model model) {
        Risk risk = new Risk();
        model.addAttribute("risk", risk);
        return "risk/newRisk";
    }

    @GetMapping("/viewRisk/{id}")
    public String viewRisk(@PathVariable(value = "id") Long id, Model model) {
        Risk risk = this.riskService.getRiskById(id);
        model.addAttribute("risk", risk);
        System.out.println(risk);
        return "risk/viewRisk";

    }
    // For Saving the Added/Updated Risk
    @PostMapping("/saveRisk")
    public String saveRisk(@ModelAttribute("risk") Risk risk) {
        // Save Risk to the Database
        riskService.saveRisk(risk);
        return "redirect:/risk";
    }

    // For Updating a Particular Risk
    @GetMapping("/showUpdateRiskForm/{id}")
    public String showUpdateRiskForm(@PathVariable(value="id") Long id, Model model) {
        // Get the Risk from the Service Using the "id"
        Risk risk = riskService.getRiskById(id);
        // Set Risk as a Model Attribute to Pre-populate the Form !
        model.addAttribute("risk", risk);
        return "risk/updateRisk";
    }

    @PostMapping("/updateRisk")
    public String updateRisk(@ModelAttribute("risk") Risk risk) {
        Risk updatedRisk = this.riskService.updateRisk(risk);
        return "redirect:/risk";
    }

    // For Deleting a Risk by "id"
    @GetMapping("/deleteRisk/{id}")
    public String deleteRisk(@PathVariable(value="id") Long id) {
        Risk risk = riskService.getRiskById(id);
        for(Policy policy : risk.getPoliciesIncludedIn()) {
            // Can't Use Utility Function For Bidirectional Synced Remove Directly Here (The Bidirectional Remove Won't Work)
            policy.getRisksInvolved().remove(risk);
        }
        risk.getPoliciesIncludedIn().clear();
        this.riskService.deleteRiskById(id);
        return "redirect:/risk";
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value="pageNo") Integer pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {

        Integer pageSize = 5;
        Page<Risk> page = riskService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Risk> listRisks = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listRisks", listRisks);

        return "risk/homeRisk";
    }
}
