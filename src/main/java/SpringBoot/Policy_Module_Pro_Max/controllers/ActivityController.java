package SpringBoot.Policy_Module_Pro_Max.controllers;

import SpringBoot.Policy_Module_Pro_Max.models.Activity;
import SpringBoot.Policy_Module_Pro_Max.models.User;
import SpringBoot.Policy_Module_Pro_Max.models.UserInfo;
import SpringBoot.Policy_Module_Pro_Max.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping
    public String showActivityPage(Model model) {
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userInfo.getUser();
        String username = user.getUsername();
        return findPaginated(username, 1, "id", "asc", model);
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(String username, @PathVariable(value = "pageNo") Integer pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {

        Integer pageSize = 8;
        Page<Activity> page = activityService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Activity> listActivity = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listActivity", listActivity);

        return "activity/homeActivity";
    }
}

