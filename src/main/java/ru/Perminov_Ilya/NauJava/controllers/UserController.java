package ru.Perminov_Ilya.NauJava.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.Perminov_Ilya.NauJava.services.UserService;

import java.security.InvalidParameterException;
import java.security.Principal;

@Controller
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/fireblob";
    }

    @GetMapping("/register")
    public String registerPage(Principal principal) {
        if (principal != null)
            return "redirect:/fireblob";

        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String login,
            @RequestParam String password,
            @RequestParam String tag,
            @RequestParam(required = false) String name,
            Model model) {
        try {
            var finalName = (name == null || name.isBlank())
                    ? tag
                    : name;

            userService.registerUser(tag, login, password, finalName);

            return "redirect:/login?registered";
        } catch (InvalidParameterException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String loginPage(Principal principal) {
        if (principal != null)
            return "redirect:/fireblob";

        return "login";
    }
}