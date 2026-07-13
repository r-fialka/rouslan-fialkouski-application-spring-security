package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.configuration.SecurityConfig;
import com.openclassrooms.paymybuddy.model.Transaction;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.security.CustomUserDetailsService;
import com.openclassrooms.paymybuddy.service.TransactionService;
import com.openclassrooms.paymybuddy.service.UserService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(SecurityConfig.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Verify that the home page is displayed.
     */
    @Test
    @DisplayName("GET /home should return home page")
    @WithMockUser(username = "john@test.com")
    void shouldDisplayHomePage() throws Exception {

        User receiver = new User();
        receiver.setId(2);
        receiver.setUserName("Mike");

        Transaction transaction = new Transaction();
        transaction.setReceiver(receiver);
        transaction.setDescription("Lunch");
        transaction.setAmount(20.0);

        when(userService.getConnections("john@test.com"))
                .thenReturn(Set.of(receiver));

        when(transactionService.getTransactions("john@test.com"))
                .thenReturn(List.of(transaction));

        mockMvc.perform(get("/home"))

                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("connections"))
                .andExpect(model().attributeExists("transactions"));

        verify(userService)
                .getConnections("john@test.com");

        verify(transactionService)
                .getTransactions("john@test.com");

    }

    /**
     * Verify successful money transfer.
     */
    @Test
    @DisplayName("POST /transfer should redirect to home page")
    @WithMockUser(username = "john@test.com")
    void shouldTransferMoney() throws Exception {

        doNothing().when(transactionService)
                .createTransaction(
                        anyString(),
                        anyInt(),
                        anyString(),
                        anyDouble()
                );

        mockMvc.perform(post("/transfer")
                        .with(csrf())
                        .param("receiverId", "2")
                        .param("description", "Lunch")
                        .param("amount", "20"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(transactionService)
                .createTransaction(
                        "john@test.com",
                        2,
                        "Lunch",
                        20.0
                );

    }

    /**
     * Verify that transfer service is invoked once.
     */
    @Test
    @DisplayName("POST /transfer should call transaction service")
    @WithMockUser(username = "john@test.com")
    void shouldCallTransactionService() throws Exception {

        doNothing().when(transactionService)
                .createTransaction(
                        anyString(),
                        anyInt(),
                        anyString(),
                        anyDouble()
                );

        mockMvc.perform(post("/transfer")
                        .with(csrf())
                        .param("receiverId", "5")
                        .param("description", "Dinner")
                        .param("amount", "75"))

                .andExpect(status().is3xxRedirection());

        verify(transactionService)
                .createTransaction(
                        "john@test.com",
                        5,
                        "Dinner",
                        75.0
                );

    }

}