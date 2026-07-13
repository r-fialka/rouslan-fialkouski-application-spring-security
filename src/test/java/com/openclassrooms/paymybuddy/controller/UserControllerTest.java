package com.openclassrooms.paymybuddy.controller;

import com.openclassrooms.paymybuddy.configuration.SecurityConfig;
import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.security.CustomUserDetailsService;
import com.openclassrooms.paymybuddy.service.UserService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Verify that the login page is displayed.
     */
    @Test
    @DisplayName("GET /login should return login page")
    @WithAnonymousUser
    void shouldDisplayLoginPage() throws Exception {

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));

    }

    /**
     * Verify that the root URL displays the login page.
     */
    @Test
    @DisplayName("GET / should return login page")
    @WithAnonymousUser
    void shouldDisplayRootLoginPage() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));

    }

    /**
     * Verify that the registration page is displayed.
     */
    @Test
    @DisplayName("GET /register should return register page")
    @WithAnonymousUser
    void shouldDisplayRegisterPage() throws Exception {

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

    }

    /**
     * Verify successful user registration.
     */
    @Test
    @DisplayName("POST /register should redirect to login page")
    @WithAnonymousUser
    void shouldRegisterUser() throws Exception {

        doNothing().when(userService).register(any(User.class));

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("userName", "John")
                        .param("email", "john@test.com")
                        .param("password", "password"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService).register(any(User.class));

    }

    /**
     * Verify that the profile page is displayed.
     */
    @Test
    @DisplayName("GET /profile should return profile page")
    @WithMockUser(username = "john@test.com")
    void shouldDisplayProfilePage() throws Exception {

        User user = new User();
        user.setUserName("John");
        user.setEmail("john@test.com");

        when(userService.getUserByEmail("john@test.com"))
                .thenReturn(user);

        mockMvc.perform(get("/profile"))

                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("editMode", false));

        verify(userService).getUserByEmail("john@test.com");

    }

    /**
     * Verify successful profile update.
     */
    @Test
    @DisplayName("POST /profile should update profile")
    @WithMockUser(username = "john@test.com")
    void shouldUpdateProfile() throws Exception {

        doNothing().when(userService).updateProfile(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString()
        );

        mockMvc.perform(post("/profile")
                        .with(csrf())
                        .param("userName", "John")
                        .param("email", "john@test.com")
                        .param("password", "password")
                        .param("confirmPassword", "password"))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile?success"));

        verify(userService).updateProfile(
                eq("john@test.com"),
                eq("John"),
                eq("john@test.com"),
                eq("password"),
                eq("password")
        );

    }

    /**
     * Verify profile update failure.
     */
    @Test
    @DisplayName("POST /profile should display error message")
    @WithMockUser(username = "john@test.com")
    void shouldDisplayProfileError() throws Exception {

        User user = new User();
        user.setUserName("John");
        user.setEmail("john@test.com");

        when(userService.getUserByEmail("john@test.com"))
                .thenReturn(user);

        doThrow(new IllegalArgumentException("Passwords do not match."))
                .when(userService)
                .updateProfile(
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString(),
                        anyString()
                );

        mockMvc.perform(post("/profile")
                        .with(csrf())
                        .param("userName", "John")
                        .param("email", "john@test.com")
                        .param("password", "password")
                        .param("confirmPassword", "123456"))

                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("editMode", true))
                .andExpect(model().attribute("error", "Passwords do not match."));

    }

    /**
     * Verify that success message is displayed.
     */
    @Test
    @DisplayName("GET /profile?success should display success message")
    @WithMockUser(username = "john@test.com")
    void shouldDisplaySuccessMessage() throws Exception {

        User user = new User();
        user.setUserName("John");
        user.setEmail("john@test.com");

        when(userService.getUserByEmail("john@test.com"))
                .thenReturn(user);

        mockMvc.perform(get("/profile")
                        .param("success", ""))

                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("editMode", false))
                .andExpect(model().attribute(
                        "success",
                        "Profile updated successfully."
                ));

        verify(userService).getUserByEmail("john@test.com");

    }

    /**
     * Verify that the add relation page is displayed.
     */
    @Test
    @DisplayName("GET /add-relation should return add relation page")
    @WithMockUser(username = "john@test.com")
    void shouldDisplayAddRelationPage() throws Exception {

        mockMvc.perform(get("/add-relation"))

                .andExpect(status().isOk())
                .andExpect(view().name("add-relation"));

    }

    /**
     * Verify successful relation creation.
     */
    @Test
    @DisplayName("POST /add-relation should add relation")
    @WithMockUser(username = "john@test.com")
    void shouldAddRelation() throws Exception {

        doNothing().when(userService)
                .addConnection(
                        "john@test.com",
                        "friend@test.com"
                );

        mockMvc.perform(post("/add-relation")
                        .with(csrf())
                        .param("email", "friend@test.com"))

                .andExpect(status().isOk())
                .andExpect(view().name("add-relation"))
                .andExpect(model().attribute(
                        "success",
                        "Relation ajoutée avec succès."
                ));

        verify(userService)
                .addConnection(
                        "john@test.com",
                        "friend@test.com"
                );

    }

    /**
     * Verify relation creation failure.
     */
    @Test
    @DisplayName("POST /add-relation should display error")
    @WithMockUser(username = "john@test.com")
    void shouldDisplayAddRelationError() throws Exception {

        doThrow(new IllegalArgumentException("User not found."))
                .when(userService)
                .addConnection(
                        anyString(),
                        anyString()
                );

        mockMvc.perform(post("/add-relation")
                        .with(csrf())
                        .param("email", "friend@test.com"))

                .andExpect(status().isOk())
                .andExpect(view().name("add-relation"))
                .andExpect(model().attribute(
                        "error",
                        "User not found."
                ));

    }

}