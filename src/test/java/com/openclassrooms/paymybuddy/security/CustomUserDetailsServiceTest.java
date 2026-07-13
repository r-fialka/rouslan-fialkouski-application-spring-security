package com.openclassrooms.paymybuddy.security;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Verify that an existing user is loaded successfully.
     */
    @Test
    @DisplayName("Should load existing user by email")
    void shouldLoadUserByUsername() {

        // Arrange

        User user = new User();

        user.setId(1);
        user.setUserName("John");
        user.setEmail("john@test.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(user));

        // Act

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("john@test.com");

        // Assert

        assertNotNull(userDetails);
        assertEquals("john@test.com", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());

        assertTrue(userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        verify(userRepository).findByEmail("john@test.com");

    }

    /**
     * Verify that an exception is thrown
     * when the user does not exist.
     */
    @Test
    @DisplayName("Should throw exception when user is not found")
    void shouldThrowExceptionWhenUserNotFound() {

        // Arrange

        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        // Act & Assert

        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> customUserDetailsService
                                .loadUserByUsername("unknown@test.com")
                );

        assertEquals("User not found.", exception.getMessage());

        verify(userRepository).findByEmail("unknown@test.com");

    }

}
