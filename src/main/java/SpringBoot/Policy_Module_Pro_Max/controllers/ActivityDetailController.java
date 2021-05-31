package SpringBoot.Policy_Module_Pro_Max.controllers;

import SpringBoot.Policy_Module_Pro_Max.models.Activity;
import SpringBoot.Policy_Module_Pro_Max.models.ActivityDetail;
import SpringBoot.Policy_Module_Pro_Max.services.ActivityDetailService;
import SpringBoot.Policy_Module_Pro_Max.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/activityDetail")
public class ActivityDetailController {

    @Autowired
    private ActivityDetailService activityDetailService;

    @Autowired
    private ActivityService activityService;

    @GetMapping("/{id}")
    public String showActivityDetails(@PathVariable(value = "id") Long id, Model model) {
        Activity activity = activityService.getActivityById(id);
        return findPaginated(activity, 1, "id", "asc", model);
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(Activity activity, @PathVariable(value = "pageNo") Integer pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {

        Integer pageSize = 5;
        Page<ActivityDetail> page = activityDetailService.findPaginatedByActivity(activity, pageNo, pageSize, sortField, sortDir);
        List<ActivityDetail> listActivityDetail = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listActivityDetail", listActivityDetail);

        return "activityDetail/homeActivityDetail";
    }
}


