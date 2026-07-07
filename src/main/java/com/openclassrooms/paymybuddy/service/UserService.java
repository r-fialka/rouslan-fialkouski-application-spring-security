package com.openclassrooms.paymybuddy.service;

import com.openclassrooms.paymybuddy.model.User;
import com.openclassrooms.paymybuddy.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

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
     * Registration
     */
    public void save(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }

    /**
     * Add friend
     */
    @Transactional
    public void addConnection(String currentUserEmail, String friendEmail) {

        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Current user not found."));

        User friend = userRepository.findByEmail(friendEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

        // Нельзя добавить самого себя
        if (currentUser.getId().equals(friend.getId())) {
            throw new IllegalArgumentException("You cannot add yourself.");
        }

        // Уже есть в друзьях
        if (currentUser.getConnections().contains(friend)) {
            throw new IllegalArgumentException("Connection already exists.");
        }

        currentUser.getConnections().add(friend);

        userRepository.save(currentUser);
    }

    @Transactional(readOnly = true)
    public Set<User> getConnections(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

        user.getConnections().size();   // принудительная инициализация

        return user.getConnections();
    }

}