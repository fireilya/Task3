package ru.Perminov_Ilya.NauJava.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/fireblob")
public class FireBlobController {
    @GetMapping
    public String fireBlob(Principal principal, Model model) {
        model.addAttribute("userName", principal != null ? principal.getName() : "");
        return "fireblob";
    }
}