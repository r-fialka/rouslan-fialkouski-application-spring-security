package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Redirect root URL to the custom login page.
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    /**
     * Display the custom login page.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Display the registration page.
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * Save a new user.
     */
    @PostMapping("/register")
    public String register(User user) {

        userService.save(user);

        return "redirect:/login";
    }

    /**
     * Home page after successful authentication.
     */
    @GetMapping("/home")
    public String home() {
        return "home";
    }

    /**
     * User profile page.
     */
    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    /**
     * Add relation page.
     */
    @GetMapping("/add-relation")
    public String addRelation() {
        return "add-relation";
    }
}