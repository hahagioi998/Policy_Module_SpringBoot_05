package SpringBoot.Policy_Module_Pro_Max.controllers;

import SpringBoot.Policy_Module_Pro_Max.models.Remedy;
import SpringBoot.Policy_Module_Pro_Max.models.Activity;
import SpringBoot.Policy_Module_Pro_Max.services.RemedyService;
import SpringBoot.Policy_Module_Pro_Max.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/remedy")
public class RemedyController {

    @Autowired
    private RemedyService remedyService;

    @Autowired
    private ActivityService activityService;

    @GetMapping("/{id}")
    public String showRemedyForActivity(@PathVariable(name = "id") Long id, Model model) {
        Activity activity = activityService.findActivityById(id);
        Remedy remedy = remedyService.getRemedyByActivity(activity);

        List<Remedy> remedyList = new ArrayList<>();
        remedyList.add(remedy);
        model.addAttribute("listRemedyDetail", remedyList);

        return "remedy/homeRemedy";
    }
}
