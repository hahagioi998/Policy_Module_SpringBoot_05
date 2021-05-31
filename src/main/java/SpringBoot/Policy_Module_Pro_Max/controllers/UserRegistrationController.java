package SpringBoot.Policy_Module_Pro_Max.controllers;

import SpringBoot.Policy_Module_Pro_Max.dto.UserRegistrationDto;
import SpringBoot.Policy_Module_Pro_Max.services.UserService;
import SpringBoot.Policy_Module_Pro_Max.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

    private UserService userService;

    public UserRegistrationController(UserService userService) {
        super();
        this.userService = userService;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto,
                                      BindingResult result) {
        User existing = userService.findByUsername(registrationDto.getUsername());
        if(existing != null) {
            result.rejectValue("username", null, "There is Already an Account Registered With That Username");
        }
        if(result.hasErrors()) {
            return "registration";
        }

        userService.save(registrationDto);

        return "redirect:/login";
    }
}

