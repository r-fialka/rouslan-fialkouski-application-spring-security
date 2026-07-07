package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String login() {
        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(User user) {

        userService.save(user);

        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/add-relation")
    public String addRelationPage() {

        return "add-relation";
    }

    @PostMapping("/add-relation")
    public String addRelation(Authentication authentication,
                              @RequestParam String email,
                              Model model) {

        try {

            userService.addConnection(authentication.getName(), email);

            model.addAttribute("success",
                    "Relation ajoutée avec succès.");

        } catch (IllegalArgumentException e) {

            model.addAttribute("error",
                    e.getMessage());

        }

        return "add-relation";
    }

}