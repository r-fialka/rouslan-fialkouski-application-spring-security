package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Set;

/**
 * Unit tests for {@link UserService}.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    /**
     * Verify that a new user is successfully registered.
     */
    @Test
    void shouldRegisterUserSuccessfully() {

        // Arrange

        User user = new User();

        user.setUserName("Ruslan");
        user.setEmail("ruslan@test.com");
        user.setPassword("123456");

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("123456"))
                .thenReturn("encodedPassword");

        // Act

        userService.register(user);

        // Assert

        assertThat(user.getPassword())
                .isEqualTo("encodedPassword");

        verify(userRepository, times(1))
                .save(user);

        verify(passwordEncoder, times(1))
                .encode("123456");

    }

    /**
     * Verify that registration fails
     * when the email already exists.
     */
    @Test
    void shouldThrowWhenEmailAlreadyExists() {

        // Arrange

        User user = new User();

        user.setUserName("Ruslan");
        user.setEmail("ruslan@test.com");
        user.setPassword("123456");

        when(userRepository.findByEmail(user.getEmail()))
                .thenReturn(Optional.of(user));

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.register(user)
                );

        assertThat(exception.getMessage())
                .isEqualTo("Email already exists.");

        verify(userRepository, never())
                .save(any(User.class));

        verify(passwordEncoder, never())
                .encode(anyString());

    }

    /**
     * Verify that a new connection is successfully added.
     */
    @Test
    void shouldAddConnectionSuccessfully() {

        // Arrange

        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setEmail("current@test.com");

        User friend = new User();
        friend.setId(2);
        friend.setEmail("friend@test.com");

        when(userRepository.findByEmail("current@test.com"))
                .thenReturn(Optional.of(currentUser));

        when(userRepository.findByEmail("friend@test.com"))
                .thenReturn(Optional.of(friend));

        // Act

        userService.addConnection(
                "current@test.com",
                "friend@test.com"
        );

        // Assert

        assertThat(currentUser.getConnections())
                .contains(friend);

        verify(userRepository)
                .save(currentUser);

    }

    /**
     * Verify that a user cannot add themselves as a connection.
     */
    @Test
    void shouldThrowWhenAddingYourself() {

        // Arrange

        User user = new User();

        user.setId(1);
        user.setEmail("user@test.com");

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.addConnection(
                                "user@test.com",
                                "user@test.com")
                );

        assertThat(exception.getMessage())
                .isEqualTo("You cannot add yourself.");

        verify(userRepository, never())
                .save(any(User.class));

    }

    /**
     * Verify that an existing connection
     * cannot be added twice.
     */
    @Test
    void shouldThrowWhenConnectionAlreadyExists() {

        // Arrange

        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setEmail("current@test.com");

        User friend = new User();
        friend.setId(2);
        friend.setEmail("friend@test.com");

        currentUser.getConnections().add(friend);

        when(userRepository.findByEmail("current@test.com"))
                .thenReturn(Optional.of(currentUser));

        when(userRepository.findByEmail("friend@test.com"))
                .thenReturn(Optional.of(friend));

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.addConnection(
                                "current@test.com",
                                "friend@test.com")
                );

        assertThat(exception.getMessage())
                .isEqualTo("Connection already exists.");

        verify(userRepository, never())
                .save(any(User.class));

    }

    /**
     * Verify that adding a connection fails
     * when the friend does not exist.
     */
    @Test
    void shouldThrowWhenFriendNotFound() {

        // Arrange

        User currentUser = new User();

        currentUser.setId(1);
        currentUser.setEmail("current@test.com");

        when(userRepository.findByEmail("current@test.com"))
                .thenReturn(Optional.of(currentUser));

        when(userRepository.findByEmail("friend@test.com"))
                .thenReturn(Optional.empty());

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.addConnection(
                                "current@test.com",
                                "friend@test.com")
                );

        assertThat(exception.getMessage())
                .isEqualTo("User not found.");

        verify(userRepository, never())
                .save(any(User.class));

    }

    /**
     * Verify that adding a connection fails
     * when the current user does not exist.
     */
    @Test
    void shouldThrowWhenCurrentUserNotFound() {

        // Arrange

        when(userRepository.findByEmail("current@test.com"))
                .thenReturn(Optional.empty());

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.addConnection(
                                "current@test.com",
                                "friend@test.com")
                );

        assertThat(exception.getMessage())
                .isEqualTo("User not found.");

        verify(userRepository, never())
                .save(any(User.class));

    }

    /**
     * Verify that a user's profile is successfully updated.
     */
    @Test
    void shouldUpdateProfileSuccessfully() {

        // Arrange

        User user = new User();

        user.setId(1);
        user.setUserName("Old Name");
        user.setEmail("old@test.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("old@test.com"))
                .thenReturn(Optional.of(user));

        // Act

        userService.updateProfile(
                "old@test.com",
                "New Name",
                "new@test.com",
                "",
                ""
        );

        // Assert

        assertThat(user.getUserName())
                .isEqualTo("New Name");

        assertThat(user.getEmail())
                .isEqualTo("new@test.com");

        assertThat(user.getPassword())
                .isEqualTo("encodedPassword");

        verify(userRepository)
                .save(user);

        verify(passwordEncoder, never())
                .encode(anyString());

    }

    /**
     * Verify that the user's password is successfully updated.
     */
    @Test
    void shouldUpdatePasswordSuccessfully() {

        // Arrange

        User user = new User();

        user.setId(1);
        user.setUserName("Ruslan");
        user.setEmail("user@test.com");
        user.setPassword("oldEncodedPassword");

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("newEncodedPassword");

        // Act

        userService.updateProfile(
                "user@test.com",
                "Ruslan",
                "user@test.com",
                "newPassword",
                "newPassword"
        );

        // Assert

        assertThat(user.getPassword())
                .isEqualTo("newEncodedPassword");

        verify(passwordEncoder)
                .encode("newPassword");

        verify(userRepository)
                .save(user);

    }

    /**
     * Verify that profile update fails
     * when passwords do not match.
     */
    @Test
    void shouldThrowWhenPasswordsDoNotMatch() {

        // Arrange

        User user = new User();

        user.setId(1);
        user.setEmail("user@test.com");

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.updateProfile(
                                "user@test.com",
                                "Ruslan",
                                "user@test.com",
                                "password1",
                                "password2")
                );

        assertThat(exception.getMessage())
                .isEqualTo("Passwords do not match.");

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));

    }

    /**
     * Verify that profile update fails
     * when the new email already exists.
     */
    @Test
    void shouldThrowWhenEmailAlreadyExistsDuringUpdate() {

        // Arrange

        User currentUser = new User();

        currentUser.setId(1);
        currentUser.setEmail("current@test.com");

        User anotherUser = new User();

        anotherUser.setId(2);
        anotherUser.setEmail("new@test.com");

        when(userRepository.findByEmail("current@test.com"))
                .thenReturn(Optional.of(currentUser));

        when(userRepository.findByEmail("new@test.com"))
                .thenReturn(Optional.of(anotherUser));

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.updateProfile(
                                "current@test.com",
                                "Ruslan",
                                "new@test.com",
                                "",
                                "")
                );

        assertThat(exception.getMessage())
                .isEqualTo("Email already exists.");

        verify(userRepository, never())
                .save(any(User.class));

    }

    /**
     * Verify that all user connections are returned.
     */
    @Test
    void shouldReturnUserConnections() {

        // Arrange

        User user = new User();

        user.setId(1);
        user.setEmail("user@test.com");

        User friend = new User();

        friend.setId(2);
        friend.setEmail("friend@test.com");

        user.getConnections().add(friend);

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        // Act

        Set<User> connections =
                userService.getConnections("user@test.com");

        // Assert

        assertThat(connections)
                .hasSize(1)
                .contains(friend);

        verify(userRepository)
                .findByEmail("user@test.com");

    }

    /**
     * Verify that getting connections fails
     * when the user does not exist.
     */
    @Test
    void shouldThrowWhenGettingConnectionsForUnknownUser() {

        // Arrange

        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.getConnections(
                                "unknown@test.com")
                );

        assertThat(exception.getMessage())
                .isEqualTo("User not found.");

        verify(userRepository)
                .findByEmail("unknown@test.com");

    }

    /**
     * Verify that a user is returned by email.
     */
    @Test
    void shouldReturnUserByEmail() {

        // Arrange

        User user = new User();

        user.setId(1);
        user.setUserName("Ruslan");
        user.setEmail("user@test.com");

        when(userRepository.findByEmail("user@test.com"))
                .thenReturn(Optional.of(user));

        // Act

        User result =
                userService.getUserByEmail("user@test.com");

        // Assert

        assertThat(result)
                .isEqualTo(user);

        verify(userRepository)
                .findByEmail("user@test.com");

    }

    /**
     * Verify that getting a user fails
     * when the email does not exist.
     */
    @Test
    void shouldThrowWhenGettingUnknownUser() {

        // Arrange

        when(userRepository.findByEmail("unknown@test.com"))
                .thenReturn(Optional.empty());

        // Act & Assert

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.getUserByEmail(
                                "unknown@test.com")
                );

        assertThat(exception.getMessage())
                .isEqualTo("User not found.");

        verify(userRepository)
                .findByEmail("unknown@test.com");

    }

    /**
     * Verify profile update without changing password.
     */
    @Test
    @DisplayName("Should update profile without changing password")
    void shouldUpdateProfileWithoutPassword() {

        User user = new User();

        user.setId(1);
        user.setUserName("John");
        user.setEmail("john@test.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(user));

        userService.updateProfile(
                "john@test.com",
                "Johnny",
                "john@test.com",
                "",
                ""
        );

        assertEquals("Johnny", user.getUserName());
        assertEquals("john@test.com", user.getEmail());

        // Пароль не должен измениться
        assertEquals("encodedPassword", user.getPassword());

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(user);

    }

    /**
     * Verify profile update without changing the password
     * when no password is provided.
     */
    @Test
    @DisplayName("Should update profile when password is null")
    void shouldUpdateProfileWithoutPasswordWhenNull() {

        User user = new User();

        user.setId(1);
        user.setUserName("John");
        user.setEmail("john@test.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("john@test.com"))
                .thenReturn(Optional.of(user));

        userService.updateProfile(
                "john@test.com",
                "Johnny",
                "john@test.com",
                null,
                null
        );

        assertEquals("Johnny", user.getUserName());
        assertEquals("john@test.com", user.getEmail());
        assertEquals("encodedPassword", user.getPassword());

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(user);

    }

}