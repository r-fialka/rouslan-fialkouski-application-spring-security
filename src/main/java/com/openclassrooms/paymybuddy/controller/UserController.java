package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles user authentication, registration,
 * profile management and connections.
 */
@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {

        this.userService = userService;

    }

    /**
     * Display login page.
     */
    @GetMapping({"/", "/login"})
    public String login() {

        return "login";

    }

    /**
     * Display registration page.
     */
    @GetMapping("/register")
    public String registerPage() {

        return "register";

    }

    /**
     * Register a new user.
     */
    @PostMapping("/register")
    public String register(User user) {

        userService.register(user);

        return "redirect:/login";

    }

    /**
     * Display user profile.
     */
    @GetMapping("/profile")
    public String profile(Authentication authentication,
                          @RequestParam(defaultValue = "false") boolean edit,
                          @RequestParam(required = false) String success,
                          Model model) {

        model.addAttribute(
                "user",
                userService.getUserByEmail(authentication.getName())
        );

        model.addAttribute(
                "editMode",
                edit
        );

        if (success != null) {

            model.addAttribute(
                    "success",
                    "Profile updated successfully."
            );

        }

        return "profile";

    }

    /**
     * Update user profile.
     */
    @PostMapping("/profile")
    public String updateProfile(Authentication authentication,
                                @RequestParam String userName,
                                @RequestParam String email,
                                @RequestParam(required = false) String password,
                                @RequestParam(required = false) String confirmPassword,
                                Model model) {

        try {

            userService.updateProfile(
                    authentication.getName(),
                    userName,
                    email,
                    password,
                    confirmPassword
            );

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "user",
                    userService.getUserByEmail(authentication.getName())
            );

            model.addAttribute(
                    "editMode",
                    true
            );

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "profile";

        }

        return "redirect:/profile?success";

    }

    /**
     * Display add relation page.
     */
    @GetMapping("/add-relation")
    public String addRelationPage() {

        return "add-relation";

    }

    /**
     * Add a new relation.
     */
    @PostMapping("/add-relation")
    public String addRelation(Authentication authentication,
                              @RequestParam String email,
                              Model model) {

        try {

            userService.addConnection(
                    authentication.getName(),
                    email
            );

            model.addAttribute(
                    "success",
                    "Relation ajoutée avec succès."
            );

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

        }

        return "add-relation";

    }

}