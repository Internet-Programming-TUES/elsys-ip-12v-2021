package org.elsys.ip.web;

import org.elsys.ip.error.UserAlreadyExistException;
import org.elsys.ip.model.User;
import org.elsys.ip.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/user/registration")
    public String showRegistrationForm(WebRequest request, Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        model.addAttribute("message", null);
        return "registration";
    }

    @PostMapping("/user/registration")
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDto userDto,
                                      HttpServletRequest request, Errors errors) {
        try {
            User registered = userService.registerNewUserAccount(userDto);
        } catch (UserAlreadyExistException uaeEx) {
            ModelAndView modelAndView = new ModelAndView("registration");
            modelAndView.addObject("message", "An account for that username/email already exists.");
            modelAndView.addObject("user", userDto);
            return modelAndView;
        }

        ModelAndView modelAndView = new ModelAndView("successRegistration");
        return modelAndView;
    }
}
