package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Provides user management and profile operations.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }

    /**
     * Register a new user.
     */
    public void register(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);

    }

    /**
     * Add a new connection.
     */
    @Transactional
    public void addConnection(String currentUserEmail,
                              String friendEmail) {

        User currentUser = findUserByEmail(currentUserEmail);
        User friend = findUserByEmail(friendEmail);

        if (currentUser.getId().equals(friend.getId())) {
            throw new IllegalArgumentException("You cannot add yourself.");
        }

        if (currentUser.getConnections().contains(friend)) {
            throw new IllegalArgumentException("Connection already exists.");
        }

        currentUser.getConnections().add(friend);

        userRepository.save(currentUser);

    }

    /**
     * Return all user connections.
     */
    @Transactional(readOnly = true)
    public Set<User> getConnections(String email) {

        User user = findUserByEmail(email);

        // Initialize lazy collection.
        user.getConnections().size();

        return user.getConnections();

    }

    /**
     * Return a user by email.
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {

        return findUserByEmail(email);

    }

    /**
     * Update profile information.
     */
    @Transactional
    public void updateProfile(String currentEmail,
                              String userName,
                              String email,
                              String password,
                              String confirmPassword) {

        User user = findUserByEmail(currentEmail);

        // Validate email.
        if (!user.getEmail().equals(email)
                && userRepository.findByEmail(email).isPresent()) {

            throw new IllegalArgumentException("Email already exists.");

        }

        // Validate password.
        if (password != null && !password.isBlank()) {

            if (!password.equals(confirmPassword)) {

                throw new IllegalArgumentException("Passwords do not match.");

            }

            user.setPassword(passwordEncoder.encode(password));

        }

        user.setUserName(userName);
        user.setEmail(email);

        userRepository.save(user);

    }

    /**
     * Find a user by email.
     */
    private User findUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

    }

}