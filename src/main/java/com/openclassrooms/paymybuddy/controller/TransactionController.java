package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.service.TransactionService;
import com.openclassrooms.paymybuddy.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TransactionController {

    private final UserService userService;
    private final TransactionService transactionService;

    public TransactionController(UserService userService,
                                 TransactionService transactionService) {

        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping("/home")
    public String home(Authentication authentication,
                       Model model) {

        String email = authentication.getName();

        model.addAttribute(
                "connections",
                userService.getConnections(email)
        );

        model.addAttribute(
                "transactions",
                transactionService.getTransactions(email)
        );

        return "home";
    }

    @PostMapping("/transfer")
    public String transfer(Authentication authentication,

                           @RequestParam Integer receiverId,

                           @RequestParam String description,

                           @RequestParam Double amount) {

        transactionService.saveTransaction(
                authentication.getName(),
                receiverId,
                description,
                amount
        );

        return "redirect:/home";
    }

}